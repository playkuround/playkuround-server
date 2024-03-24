package com.playkuround.playkuroundserver.domain.user.api;

import com.playkuround.playkuroundserver.domain.common.AppVersion;
import com.playkuround.playkuroundserver.domain.common.SystemCheck;
import com.playkuround.playkuroundserver.domain.user.api.response.UserGameHighestScoreResponse;
import com.playkuround.playkuroundserver.domain.user.api.response.UserNotificationResponse;
import com.playkuround.playkuroundserver.domain.user.api.response.UserProfileResponse;
import com.playkuround.playkuroundserver.domain.user.application.UserProfileService;
import com.playkuround.playkuroundserver.domain.user.domain.HighestScore;
import com.playkuround.playkuroundserver.domain.user.dto.UserNotification;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserProfileApi {

    private final UserProfileService userProfileService;

    @GetMapping
    @Operation(summary = "프로필 얻기", description = "로그인 유저의 기본 정보를 얻습니다.")
    public ApiResponse<UserProfileResponse> getUserProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiUtils.success(UserProfileResponse.from(userDetails.getUser()));
    }

    @GetMapping("/game-score")
    @Operation(summary = "게임별 최고 점수 얻기", description = "로그인 유저의 게임별 최고 점수를 얻습니다. 플레이한적이 없는 게임은 null이 반환됩니다.")
    public ApiResponse<UserGameHighestScoreResponse> getUserGameHighestScore(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        HighestScore userGameHighestScore = userProfileService.getUserGameHighestScore(userDetails.getUser());
        return ApiUtils.success(UserGameHighestScoreResponse.from(userGameHighestScore));
    }

    @GetMapping("/availability")
    @Operation(summary = "해당 닉네임이 사용 가능한지 체크", description = "사용 가능하다면 true가 반환됩니다.")
    public ApiResponse<Boolean> isAvailableNickname(@RequestParam("nickname") String nickname) {
        boolean isAvailable = userProfileService.isAvailableNickname(nickname);
        return ApiUtils.success(isAvailable);
    }

    @GetMapping("/notification")
    @Operation(summary = "유저 알림 얻기",
            description = "유저 개인 알림을 얻습니다. 저장된 메시지는 (정상적인) 호출 이후 삭제됩니다.<br>" +
                    "=== name 명 리스트(new_badge는 description도 중요) ===<br>" +
                    "1. 시스템 점검 중일 때(단독으로만 반환): system_check<br>" +
                    "2. 앱 버전 업데이트가 필요할 때(단독으로만 반환): update<br>" +
                    "3. 새로운 뱃지 획득: new_badge(MONTHLY_RANKING_1, MONTHLY_RANKING_2, MONTHLY_RANKING_3, COLLEGE_OF_BUSINESS_ADMINISTRATION_100_AND_FIRST_PLACE)<br>" +
                    "4. 개인 알림: alarm",
            parameters = {
                    @Parameter(name = "version", description = "현재 앱 버전", example = "2.0.2", required = true),
                    @Parameter(name = "os", description = "모바일 운영체제(android 또는 ios)", example = "android")
            }
    )
    public ApiResponse<List<UserNotificationResponse>> getNotification(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                       @RequestParam("version") String appVersion,
                                                                       @RequestParam(name = "os", required = false, defaultValue = "android") String os) {
        List<UserNotificationResponse> response;
        if (!SystemCheck.isSystemAvailable()) {
            response = UserNotificationResponse.from(UserNotificationResponse.NotificationEnum.SYSTEM_CHECK);
        }
        else if (!AppVersion.isLatestUpdatedVersion(os, appVersion)) {
            response = UserNotificationResponse.from(UserNotificationResponse.NotificationEnum.UPDATE);
        }
        else {
            List<UserNotification> notificationList = userProfileService.getNotification(userDetails.getUser());
            response = UserNotificationResponse.from(notificationList);
        }
        return ApiUtils.success(response);
    }

}
