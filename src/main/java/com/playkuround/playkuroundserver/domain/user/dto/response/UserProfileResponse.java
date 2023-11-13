package com.playkuround.playkuroundserver.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserProfileResponse {

    @Schema(description = "이메일", example = "tester@konkuk.ac.kr", requiredMode = RequiredMode.REQUIRED)
    private String email;
    @Schema(description = "닉네임", example = "tester12", requiredMode = RequiredMode.REQUIRED)
    private String nickname;
    @Schema(description = "학과", example = "컴퓨터공학부", requiredMode = RequiredMode.REQUIRED)
    private String major;
    @Schema(description = "연속출석일 수", example = "13", requiredMode = RequiredMode.REQUIRED)
    private Integer consecutiveAttendanceDays;
    @Schema(description = "자신의 토탈 스코어 최고점", example = "1500", requiredMode = RequiredMode.REQUIRED)
    private Long highestScore;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Schema(description = "최근 출석 날짜. 출석한 적이 없다면 회원가입일 기준 1일 전으로 세팅됨", example = "2023-11-13 13:12:31", requiredMode = RequiredMode.REQUIRED)
    private LocalDateTime lastAttendanceDate;

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .major(user.getMajor().name())
                .consecutiveAttendanceDays(user.getConsecutiveAttendanceDays())
                .lastAttendanceDate(user.getLastAttendanceDate())
                .highestScore(user.getHighestScore())
                .build();
    }
}
