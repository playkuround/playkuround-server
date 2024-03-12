package com.playkuround.playkuroundserver.domain.auth.token.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthVerifyToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String authVerifyToken;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    public AuthVerifyToken(String authVerifyToken, LocalDateTime expiredAt) {
        this.authVerifyToken = authVerifyToken;
        this.expiredAt = expiredAt;
    }
}

