package com.hhplus.concert_ticketing.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;

@Component
public class LoggingFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            logRequest(request);
            filterChain.doFilter(request, responseWrapper);
        } finally {
            logResponse(responseWrapper);
            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequest(HttpServletRequest request) {
        StringBuilder logMessage = new StringBuilder("Request Details:\n");
        logMessage.append("Method: ").append(request.getMethod()).append("\n");
        logMessage.append("Request URI: ").append(request.getRequestURI()).append("\n");
        logMessage.append("Headers:\n");

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            logMessage.append(headerName).append(": ").append(request.getHeader(headerName)).append("\n");
        }

        logger.info(logMessage.toString());
    }

    private void logResponse(ContentCachingResponseWrapper responseWrapper) {
        String responseBody = new String(responseWrapper.getContentAsByteArray());
        String logMessage = "Response Status: " + responseWrapper.getStatus() +
                "\nResponse Body: " + responseBody;

        logger.info(logMessage);
    }
}
