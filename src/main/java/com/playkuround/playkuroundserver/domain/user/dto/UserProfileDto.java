package com.playkuround.playkuroundserver.domain.user.dto;

import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.*;

public class UserProfileDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private String email;

        private String nickname;

        private String major;

        public static UserProfileDto.Response of(User user) {
            return UserProfileDto.Response.builder()
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .major(user.getMajor().name())
                    .build();
        }

    }

}
