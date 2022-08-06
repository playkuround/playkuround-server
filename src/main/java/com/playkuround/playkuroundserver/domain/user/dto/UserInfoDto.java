package com.playkuround.playkuroundserver.domain.user.dto;

import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.*;

public class UserInfoDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private String email;

        private String nickname;

        private String major;

        public static UserInfoDto.Response of(User user) {
            return UserInfoDto.Response.builder()
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .major(user.getMajor().name())
                    .build();
        }

    }

}
