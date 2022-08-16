package com.playkuround.playkuroundserver.domain.auth.token.domain;

public enum TokenType {

    ACCESS, REFRESH;

    public static boolean isAccessToken(String tokenType) {
        return TokenType.ACCESS.name().equals(tokenType);
    }

}
