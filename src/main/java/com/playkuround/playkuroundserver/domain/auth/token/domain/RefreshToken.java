package com.playkuround.playkuroundserver.domain.auth.token.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@RedisHash(value = "refreshToken")
@Getter
public class RefreshToken {

    @Id
    private final String userEmail;

    private final String refreshToken;

    @TimeToLive(unit = TimeUnit.DAYS)
    private final Integer timeToLive;

    @Builder
    public RefreshToken(String userEmail, String refreshToken, Integer timeToLive) {
        this.userEmail = userEmail;
        this.refreshToken = refreshToken;
        this.timeToLive = timeToLive;
    }

    public static RefreshToken of(String userEmail, String refreshToken, Integer timeToLive) {
        return RefreshToken.builder()
                .refreshToken(refreshToken)
                .userEmail(userEmail)
                .timeToLive(timeToLive)
                .build();
    }

}
