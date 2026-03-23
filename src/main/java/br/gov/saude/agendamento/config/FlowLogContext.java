package br.gov.saude.agendamento.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class FlowLogContext {

    private static final int MAX_SQL_LENGTH = 1_500;
    private static final int MAX_RESPONSE_LENGTH = 3_000;
    private static final String NOT_AVAILABLE = "-";
    private static final ThreadLocal<List<String>> SQL_STATEMENTS = ThreadLocal.withInitial(ArrayList::new);
    private static final ThreadLocal<String> RESPONSE_SUMMARY = new ThreadLocal<>();

    private FlowLogContext() {
    }

    public static void reset() {
        SQL_STATEMENTS.remove();
        RESPONSE_SUMMARY.remove();
    }

    public static void addSql(String sql) {
        if (sql == null || sql.isBlank()) {
            return;
        }
        SQL_STATEMENTS.get().add(truncate(normalize(sql), MAX_SQL_LENGTH));
    }

    public static String getSqlSummary() {
        List<String> sqlStatements = SQL_STATEMENTS.get();
        if (sqlStatements == null || sqlStatements.isEmpty()) {
            return NOT_AVAILABLE;
        }
        return sqlStatements.stream().collect(Collectors.joining(" | "));
    }

    public static void setResponseSummary(String responseSummary) {
        if (responseSummary == null || responseSummary.isBlank()) {
            RESPONSE_SUMMARY.set(NOT_AVAILABLE);
            return;
        }
        RESPONSE_SUMMARY.set(truncate(responseSummary, MAX_RESPONSE_LENGTH));
    }

    public static String getResponseSummary() {
        return Optional.ofNullable(RESPONSE_SUMMARY.get())
                .filter(value -> !value.isBlank())
                .orElse(NOT_AVAILABLE);
    }

    public static void clear() {
        reset();
    }

    private static String normalize(String sql) {
        return sql.replaceAll("\\s+", " ").trim();
    }

    private static String truncate(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength) + "...";
    }
}

