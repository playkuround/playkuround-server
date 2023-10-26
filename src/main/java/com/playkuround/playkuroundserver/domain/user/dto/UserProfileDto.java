package com.playkuround.playkuroundserver.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class UserProfileDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {

        private String email;
        private String nickname;
        private String major;
        private Integer ConsecutiveAttendanceDays;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime lastAttendanceDate;

        public static UserProfileDto.Response from(User user) {
            return Response.builder()
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .major(user.getMajor().name())
                    .ConsecutiveAttendanceDays(user.getConsecutiveAttendanceDays())
                    .lastAttendanceDate(user.getLastAttendanceDate())
                    .build();
        }

    }

}
