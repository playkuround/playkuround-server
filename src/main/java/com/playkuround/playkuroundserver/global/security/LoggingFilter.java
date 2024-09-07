package com.playkuround.playkuroundserver.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!isAsyncDispatch(request)) {
            logRequest(request);
        }
        filterChain.doFilter(request, response);
    }

    private void logRequest(HttpServletRequest request) {
        if (isNotLogFilterTarget(request)) {
            return;
        }

        String queryString = request.getQueryString();
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("Request : {} uri=[{}] content-type=[{}] token=[{}]",
                request.getMethod(),
                queryString == null ? request.getRequestURI() : request.getRequestURI() + "?" + queryString,
                request.getContentType(),
                bearerToken);
    }

    private boolean isNotLogFilterTarget(HttpServletRequest request) {
        if (request.getRequestURI() == null) {
            return false;
        }
        String requestURI = request.getRequestURI();
        return requestURI.contains("/prometheus") || requestURI.equals("/api/system-available");
    }


}


