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

    public static LandmarkHighestScoreUserResponse from(LandmarkHighestScoreUser landmarkHighestScoreUser) {
        if (landmarkHighestScoreUser.isHasResult()) {
            return new LandmarkHighestScoreUserResponse(landmarkHighestScoreUser.getNickname(), landmarkHighestScoreUser.getScore());
        }
        return new LandmarkHighestScoreUserResponse();
    }
}
