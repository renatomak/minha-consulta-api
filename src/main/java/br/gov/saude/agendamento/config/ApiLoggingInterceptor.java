package br.gov.saude.agendamento.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;
import java.util.UUID;

@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ApiLoggingInterceptor.class);
    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String REQUEST_ID_MDC_KEY = "requestId";
    private static final String START_TIME_ATTR = "apiLoggingStartNanos";
    private static final String REQUEST_ID_ATTR = "apiLoggingRequestId";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        FlowLogContext.reset();

        String requestId = Optional.ofNullable(request.getHeader(REQUEST_ID_HEADER))
                .filter(value -> !value.isBlank())
                .orElse(UUID.randomUUID().toString());

        request.setAttribute(START_TIME_ATTR, System.nanoTime());
        request.setAttribute(REQUEST_ID_ATTR, requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);

        MDC.put(REQUEST_ID_MDC_KEY, requestId);

        log.info("HTTP_IN requestId={} method={} path={} query={} handler={} remoteAddr={}",
                requestId,
                request.getMethod(),
                request.getRequestURI(),
                Optional.ofNullable(request.getQueryString()).orElse(""),
                resolveHandlerName(handler),
                request.getRemoteAddr());

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                @Nullable Exception ex) {
        String requestId = Optional.ofNullable((String) request.getAttribute(REQUEST_ID_ATTR))
                .orElse("-");

        Object startRaw = request.getAttribute(START_TIME_ATTR);
        long durationMs = startRaw instanceof Long startNanos
                ? (System.nanoTime() - startNanos) / 1_000_000
                : -1;

        String exceptionName = ex == null ? "-" : ex.getClass().getSimpleName();

        log.info("HTTP_OUT requestId={} method={} path={} status={} durationMs={} handler={} exception={} sqlExecutada={} response={}",
                requestId,
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                durationMs,
                resolveHandlerName(handler),
                exceptionName,
                FlowLogContext.getSqlSummary(),
                FlowLogContext.getResponseSummary());

        FlowLogContext.clear();
        MDC.remove(REQUEST_ID_MDC_KEY);
    }

    private String resolveHandlerName(Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            return handlerMethod.getBeanType().getSimpleName() + "#" + handlerMethod.getMethod().getName();
        }
        return handler.getClass().getSimpleName();
    }
}


