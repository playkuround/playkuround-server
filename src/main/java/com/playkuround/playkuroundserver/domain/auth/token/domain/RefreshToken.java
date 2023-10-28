package com.playkuround.playkuroundserver.domain.auth.token.domain;

import jakarta.validation.constraints.NotNull;
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

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private final Long timeToLive;

    @Builder
    public RefreshToken(@NotNull String userEmail, @NotNull String refreshToken, @NotNull Long timeToLive) {
        this.userEmail = userEmail;
        this.refreshToken = refreshToken;
        this.timeToLive = timeToLive;
    }

}
