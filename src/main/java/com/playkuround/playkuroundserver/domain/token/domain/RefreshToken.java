package com.playkuround.playkuroundserver.domain.token.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.util.Date;

@RedisHash("refreshToken")
@AllArgsConstructor
@Getter
@Builder
public class RefreshToken {

    @Id
    private String id;

    private String refreshToken;

    private Date expiredAt;

    public static RefreshToken of(String email, String refreshToken, Date expiredAt) {
        return RefreshToken.builder()
                .id(email)
                .refreshToken(refreshToken)
                .expiredAt(expiredAt)
                .build();
    }

}

