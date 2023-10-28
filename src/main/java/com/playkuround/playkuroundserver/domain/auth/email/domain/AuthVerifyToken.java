package com.playkuround.playkuroundserver.domain.auth.email.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@RedisHash(value = "authVerifyToken")
public class AuthVerifyToken {

    @Id
    private String authVerifyToken;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private final Integer timeToLive;

    @Builder
    public AuthVerifyToken(Integer timeToLive) {
        this.authVerifyToken = UUID.randomUUID().toString();
        this.timeToLive = timeToLive;
    }
}

