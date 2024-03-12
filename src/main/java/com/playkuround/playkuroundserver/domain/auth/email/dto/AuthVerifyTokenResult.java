package com.playkuround.playkuroundserver.domain.auth.email.dto;

public record AuthVerifyTokenResult(String authVerifyToken) implements AuthVerifyEmailResult {
}
