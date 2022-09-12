package com.playkuround.playkuroundserver.domain.auth.email.domain;

import com.playkuround.playkuroundserver.domain.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthEmail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String target;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Builder
    public AuthEmail(String target, String code, LocalDateTime expiredAt) {
        this.code = code;
        this.target = target;
        this.expiredAt = expiredAt;
    }

    public static AuthEmail createAuthEmail(String target, String code, LocalDateTime expireAt) {
        return AuthEmail.builder()
                .target(target)
                .code(code)
                .expiredAt(expireAt)
                .build();
    }

}
