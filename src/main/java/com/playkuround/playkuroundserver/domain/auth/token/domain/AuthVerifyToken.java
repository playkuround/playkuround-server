package com.playkuround.playkuroundserver.domain.auth.token.domain;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash(value = "authVerifyToken")
public class AuthVerifyToken {

    @Id
    private String authVerifyToken;

    @TimeToLive
    private final Integer timeToLive;

    public AuthVerifyToken(String authVerifyToken, Integer timeToLive) {
        this.authVerifyToken = authVerifyToken;
        this.timeToLive = timeToLive;
    }
}

