package com.playkuround.playkuroundserver.domain.landmark.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class LandmarkHighestScoreUser {

    @Schema(description = "사용자 닉네임", example = "내가제일이다")
    private String nickname;

    @Schema(description = "최고 점수", example = "294")
    private Long score;

    public static LandmarkHighestScoreUser createEmptyResponse() {
        return new LandmarkHighestScoreUser(null, null);
    }
}
