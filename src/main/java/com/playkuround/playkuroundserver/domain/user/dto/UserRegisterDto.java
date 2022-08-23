package com.playkuround.playkuroundserver.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.playkuround.playkuroundserver.domain.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;

public class UserRegisterDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;

        @NotEmpty(message = "닉네임은 필수값입니다.")
        @Length(min = 2, max = 8, message = "닉네임은 2글자 이상 8글자 이하여야 합니다.")
        @Pattern(regexp = "^[0-9a-zA-Z가-힣]*$", message = "닉네임은 한글, 영어, 숫자만 허용됩니다.")
        private String nickname;

        @NotEmpty(message = "학과는 필수값입니다.")
        private String major;

        public User toEntity() {
            return User.builder()
                    .email(email)
                    .nickname(nickname)
                    .major(Major.valueOf(major))
                    .build();
        }

    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private String grantType;

        private String accessToken;

        @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
        private Date accessTokenExpiredAt;

        private String refreshToken;

        @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
        private Date refreshTokenExpiredAt;

        public static UserRegisterDto.Response of(TokenDto tokenDto) {
            return Response.builder()
                    .grantType(tokenDto.getGrantType())
                    .accessToken(tokenDto.getAccessToken())
                    .accessTokenExpiredAt(tokenDto.getAccessTokenExpiredAt())
                    .refreshToken(tokenDto.getRefreshToken())
                    .refreshTokenExpiredAt(tokenDto.getRefreshTokenExpiredAt())
                    .build();
        }

    }

}
