package br.gov.saude.agendamento.config;

import org.hibernate.resource.jdbc.spi.StatementInspector;

public class JpaSqlCaptureInspector implements StatementInspector {

	@Override
	public String inspect(String sql) {
		FlowLogContext.addSql(sql);
		return sql;
	}
}

