package com.playkuround.playkuroundserver.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    private static void logRequest(HttpServletRequest request) {
        if (request.getRequestURI() != null && request.getRequestURI().contains("/prometheus")) {
            return;
        }
        String queryString = request.getQueryString();
        log.info("Request : {} uri=[{}] content-type=[{}]", request.getMethod(), queryString == null ? request.getRequestURI() : request.getRequestURI() + queryString, request.getContentType());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!isAsyncDispatch(request)) {
            logRequest(request);
        }
        filterChain.doFilter(request, response);
    }

}


