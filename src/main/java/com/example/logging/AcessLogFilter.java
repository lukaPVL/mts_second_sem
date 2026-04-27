package com.example.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@Order(2)
public class AcessLogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        Long startTime = System.currentTimeMillis();

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        try {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            String traceId = MDC.get("traceId");
            log.info("HTTP {} {} -> status={} timeMs={} trace={}",
                    httpServletRequest.getMethod(),
                    httpServletRequest.getRequestURI(),
                    httpServletResponse.getStatus(),
                    duration,
                    traceId);
        }
    }

    public String maskToken(String token) {
        if (token == null || token.isBlank()) {
            return "***";
        }
        if (token.length() < 12) {
            return "***";
        }
        return token.substring(0, 6) +
                "***" +
                token.substring(token.length() - 6);
    }
}
