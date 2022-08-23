package com.playkuround.playkuroundserver.domain.user.domain;

import com.playkuround.playkuroundserver.domain.common.BaseTimeEntity;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.global.util.DateTimeUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Column(nullable = false)
    @Length(min = 2, max = 8)
    @Pattern(regexp = "^[0-9a-zA-Z가-힣]*$")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Major major;

    private String refreshToken;

    private LocalDateTime refreshTokenExpiredAt;

    @Builder
    public User(String email, String nickname, Major major, String refreshToken) {
        this.email = email;
        this.nickname = nickname;
        this.major = major;
        this.refreshToken = refreshToken;
    }

    public void updateRefreshToken(TokenDto tokenDto) {
        this.refreshToken = tokenDto.getRefreshToken();
        this.refreshTokenExpiredAt = DateTimeUtils.convertToLocalDateTime(tokenDto.getRefreshTokenExpiredAt());
    }

    // 로그아웃을 위한 리프레시 토큰 만료시키기
    public void revokeRefreshToken() {
        this.refreshTokenExpiredAt = LocalDateTime.now();
    }

}
