package com.playkuround.playkuroundserver.domain.auth.token.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Builder
    public RefreshToken(@NotNull String userEmail, @NotNull String refreshToken, @NotNull LocalDateTime expiredAt) {
        this.userEmail = userEmail;
        this.refreshToken = refreshToken;
        this.expiredAt = expiredAt;
    }

}
