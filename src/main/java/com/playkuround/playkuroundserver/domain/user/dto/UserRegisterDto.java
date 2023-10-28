package com.playkuround.playkuroundserver.domain.user.dto;

import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.domain.user.domain.Role;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.validation.ValidEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

public class UserRegisterDto {

    @Getter
    @NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    @AllArgsConstructor
    public static class Request {

        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;

        @NotBlank(message = "닉네임은 필수값입니다.")
        @Length(min = 2, max = 8, message = "닉네임은 2글자 이상 8글자 이하여야 합니다.")
        @Pattern(regexp = "^[0-9a-zA-Z가-힣]*$", message = "닉네임은 한글, 영어, 숫자만 허용됩니다.")
        private String nickname;

        @NotBlank(message = "학과는 필수값입니다.")
        @ValidEnum(enumClass = Major.class, message = "잘못된 학과명입니다.")
        private String major;

        public User toEntity(Role role) {
            return User.builder()
                    .email(email)
                    .nickname(nickname)
                    .major(Major.valueOf(major))
                    .role(role)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {

        private String grantType;
        private String accessToken;
        private String refreshToken;

        public static UserRegisterDto.Response from(TokenDto tokenDto) {
            return Response.builder()
                    .grantType(tokenDto.getGrantType())
                    .accessToken(tokenDto.getAccessToken())
                    .refreshToken(tokenDto.getRefreshToken())
                    .build();
        }

    }

}
