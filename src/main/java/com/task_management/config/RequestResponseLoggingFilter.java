package com.task_management.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private static final int MAX_PAYLOAD_LENGTH = 2000;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        HttpServletRequest requestToUse = wrapRequest(request);
        HttpServletResponse responseToUse = wrapResponse(response);

        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(requestToUse, responseToUse);
        } finally {
            if (!isAsyncStarted(requestToUse)) {
                logRequest(requestToUse, requestId);
                logResponse(responseToUse, requestId, System.currentTimeMillis() - startTime);
            }
            if (responseToUse instanceof ContentCachingResponseWrapper wrapper) {
                wrapper.copyBodyToResponse();
            }
        }
    }

    private HttpServletRequest wrapRequest(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return request;
        }
        return new ContentCachingRequestWrapper(request);
    }

    private HttpServletResponse wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return response;
        }
        return new ContentCachingResponseWrapper(response);
    }

    private void logRequest(HttpServletRequest request, String requestId) {
        String queryString = request.getQueryString();
        String requestUri = request.getRequestURI();
        if (queryString != null) {
            requestUri += "?" + queryString;
        }

        String payload = "";
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            payload = getPayload(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding(), wrapper.getContentType());
        }

        logger.info(
                "Incoming request [{}]: method={}, uri={}, remoteAddress={}, payload={}",
                requestId,
                request.getMethod(),
                requestUri,
                request.getRemoteAddr(),
                payload);
    }

    private void logResponse(HttpServletResponse response, String requestId, long durationMs) {
        String payload = "";
        if (response instanceof ContentCachingResponseWrapper wrapper) {
            payload = getPayload(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding(), wrapper.getContentType());
        }

        logger.info(
                "Outgoing response [{}]: status={}, durationMs={}, payload={}",
                requestId,
                response.getStatus(),
                durationMs,
                payload);
    }

    private String getPayload(byte[] buf, String encoding, String contentType) {
        if (buf == null || buf.length == 0) {
            return "<empty>";
        }
        if (!isReadableContentType(contentType)) {
            return "<binary or large payload omitted>";
        }

        Charset charset = StandardCharsets.UTF_8;
        if (encoding != null && !encoding.isBlank()) {
            try {
                charset = Charset.forName(encoding);
            } catch (IllegalArgumentException ex) {
                logger.trace("Falling back to UTF-8 due to unsupported charset {}", encoding, ex);
            }
        }
        String payload = new String(buf, charset);
        if (payload.length() > MAX_PAYLOAD_LENGTH) {
            return payload.substring(0, MAX_PAYLOAD_LENGTH) + "...(truncated)";
        }
        return payload;
    }

    private boolean isReadableContentType(String contentType) {
        if (contentType == null) {
            return false;
        }

        String lowerCaseContentType = contentType.toLowerCase(Locale.ROOT);
        return lowerCaseContentType.startsWith("text")
                || lowerCaseContentType.contains("json")
                || lowerCaseContentType.contains("xml")
                || lowerCaseContentType.contains("x-www-form-urlencoded")
                || lowerCaseContentType.contains("javascript")
                || lowerCaseContentType.contains("html");
    }
}
