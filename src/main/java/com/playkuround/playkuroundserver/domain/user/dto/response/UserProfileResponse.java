package com.playkuround.playkuroundserver.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserProfileResponse {

    private String email;
    private String nickname;
    private String major;
    private Integer ConsecutiveAttendanceDays;
    private Long highestScore;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime lastAttendanceDate;

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .major(user.getMajor().name())
                .ConsecutiveAttendanceDays(user.getConsecutiveAttendanceDays())
                .lastAttendanceDate(user.getLastAttendanceDate())
                .highestScore(user.getHighestScore())
                .build();
    }
}
