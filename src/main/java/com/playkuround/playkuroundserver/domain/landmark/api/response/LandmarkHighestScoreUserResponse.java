package com.playkuround.playkuroundserver.domain.landmark.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.playkuround.playkuroundserver.domain.landmark.dto.LandmarkHighestScoreUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class LandmarkHighestScoreUserResponse {

    @Schema(description = "사용자 닉네임", example = "내가제일이다")
    private String nickname;

    @Schema(description = "최고 점수", example = "294")
    private Long score;

    @Schema(description = "해당 사용자의 프로필 뱃지", example = "COLLEGE_OF_ENGINEERING")
    private String profileBadge;

    public static LandmarkHighestScoreUserResponse from(LandmarkHighestScoreUser firstUserData) {
        String badgeTypeName = firstUserData.badgeType() == null ? null : firstUserData.badgeType().name();
        return new LandmarkHighestScoreUserResponse(firstUserData.nickname(), firstUserData.score(), badgeTypeName);
    }

    public static LandmarkHighestScoreUserResponse createEmptyResponse() {
        return new LandmarkHighestScoreUserResponse();
    }
}
