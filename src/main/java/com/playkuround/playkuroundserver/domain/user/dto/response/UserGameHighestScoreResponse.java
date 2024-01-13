package com.playkuround.playkuroundserver.domain.user.dto.response;

import com.playkuround.playkuroundserver.domain.user.domain.HighestScore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserGameHighestScoreResponse {

    @Schema(description = "토탈 최고점수", example = "1500", requiredMode = RequiredMode.REQUIRED)
    private Long highestTotalScore;
    @Schema(description = "퀴즈 최고점수", example = "13", requiredMode = RequiredMode.REQUIRED)
    private Long highestQuizScore;
    @Schema(description = "시간 맞추기 최고점수", example = "43", requiredMode = RequiredMode.REQUIRED)
    private Long highestTimeScore;
    @Schema(description = "달 깨기 최고점수", example = "23", requiredMode = RequiredMode.REQUIRED)
    private Long highestMoonScore;
    @Schema(description = "카드 뒤집기 최고점수", example = "52", requiredMode = RequiredMode.REQUIRED)
    private Long highestCardScore;
    @Schema(description = "덕쿠를 잡아라 최고점수", example = "13", requiredMode = RequiredMode.REQUIRED)
    private Long highestCatchScore;
    @Schema(description = "홍예교 타이밍 맞추기 최고점수", example = "52", requiredMode = RequiredMode.REQUIRED)
    private Long highestHongBridgeScore;
    @Schema(description = "수강신청 올 클릭 최고점수", example = "122", requiredMode = RequiredMode.REQUIRED)
    private Long highestAllClearScore;
    @Schema(description = "미생물 피하기 최고점수", example = "524", requiredMode = RequiredMode.REQUIRED)
    private Long highestMicrobeScore;

    public static UserGameHighestScoreResponse from(HighestScore highestScore) {
        if (highestScore == null) return new UserGameHighestScoreResponse();
        return UserGameHighestScoreResponse.builder()
                .highestTotalScore(highestScore.getHighestTotalScore())
                .highestQuizScore(highestScore.getHighestQuizScore())
                .highestTimeScore(highestScore.getHighestTimeScore())
                .highestMoonScore(highestScore.getHighestMoonScore())
                .highestCardScore(highestScore.getHighestCardScore())
                .highestCatchScore(highestScore.getHighestCatchScore())
                .highestHongBridgeScore(highestScore.getHighestHongBridgeScore())
                .highestAllClearScore(highestScore.getHighestAllClearScore())
                .highestMicrobeScore(highestScore.getHighestMicrobeScore())
                .build();
    }
}
