package com.playkuround.playkuroundserver.domain.user.api.response;

import com.playkuround.playkuroundserver.domain.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserProfileResponse {

    @Schema(description = "이메일", example = "tester@konkuk.ac.kr", requiredMode = RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "닉네임", example = "tester12", requiredMode = RequiredMode.REQUIRED)
    private String nickname;

    @Schema(description = "학과", example = "컴퓨터공학부", requiredMode = RequiredMode.REQUIRED)
    private String major;

    @Schema(description = "자신의 토탈 스코어 최고점(점수가 없다면 null 리턴)", example = "1500")
    private Long highestScore;

    @Schema(description = "자신의 토탈 등수 최고점(등수가 없다면 null 리턴)", example = "13")
    private Long highestRank;

    @Schema(description = "출석한 횟수", example = "28", requiredMode = RequiredMode.REQUIRED)
    private int attendanceDays;

    @Schema(description = "프로필 뱃지(프로필 뱃지가 없다면 null 리턴)", example = "MONTHLY_RANKING_1")
    private String profileBadge;

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .major(user.getMajor().name())
                .attendanceDays(user.getAttendanceDays())
                .highestScore(user.getHighestScore() == null ? null : user.getHighestScore().getHighestTotalScore())
                .highestRank(user.getHighestScore() == null ? null : user.getHighestScore().getHighestTotalRank())
                .profileBadge(user.getProfileBadge() == null ? null : user.getProfileBadge().name())
                .build();
    }
}
