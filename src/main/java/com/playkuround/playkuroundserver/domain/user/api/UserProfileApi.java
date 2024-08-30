package com.playkuround.playkuroundserver.domain.user.api;

import com.playkuround.playkuroundserver.domain.appversion.application.AppVersionService;
import com.playkuround.playkuroundserver.domain.appversion.domain.OperationSystem;
import com.playkuround.playkuroundserver.domain.badge.api.request.ProfileBadgeRequest;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.exception.BadgeTypeNotFoundException;
import com.playkuround.playkuroundserver.domain.systemcheck.application.SystemCheckService;
import com.playkuround.playkuroundserver.domain.user.api.response.UserGameHighestScoreResponse;
import com.playkuround.playkuroundserver.domain.user.api.response.UserNotificationResponse;
import com.playkuround.playkuroundserver.domain.user.api.response.UserProfileResponse;
import com.playkuround.playkuroundserver.domain.user.application.UserProfileService;
import com.playkuround.playkuroundserver.domain.user.domain.HighestScore;
import com.playkuround.playkuroundserver.domain.user.domain.Notification;
import com.playkuround.playkuroundserver.domain.user.domain.NotificationEnum;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserProfileApi {

    private final UserProfileService userProfileService;
    private final AppVersionService appVersionService;
    private final SystemCheckService systemCheckService;

    @GetMapping
    @Operation(summary = "프로필 얻기", description = "로그인 유저의 기본 정보를 얻습니다.")
    public ApiResponse<UserProfileResponse> getUserProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiUtils.success(UserProfileResponse.from(userDetails.getUser()));
    }

    @GetMapping("game-score")
    @Operation(summary = "게임별 최고 점수 얻기", description = "로그인 유저의 게임별 최고 점수를 얻습니다. 플레이한적이 없는 게임은 null이 반환됩니다.")
    public ApiResponse<UserGameHighestScoreResponse> getUserGameHighestScore(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        HighestScore userGameHighestScore = userProfileService.getUserGameHighestScore(userDetails.getUser());
        return ApiUtils.success(UserGameHighestScoreResponse.from(userGameHighestScore));
    }

    @GetMapping("availability")
    @Operation(summary = "해당 닉네임이 사용 가능한지 체크", description = "사용 가능하다면 true가 반환됩니다.")
    public ApiResponse<Boolean> isAvailableNickname(@RequestParam("nickname") String nickname) {
        boolean isAvailable = userProfileService.isAvailableNickname(nickname);
        return ApiUtils.success(isAvailable);
    }

    @PostMapping("profile-badge")
    @Operation(summary = "프로필 배지 설정", description = "사용자 프로필 배지를 설정합니다.")
    public ApiResponse<Void> setProfileBadge(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @RequestBody @Valid ProfileBadgeRequest request) {
        BadgeType badgeType = BadgeType.fromString(request.getProfileBadge())
                .orElseThrow(BadgeTypeNotFoundException::new);

        userProfileService.setProfileBadge(userDetails.getUser(), badgeType);
        return ApiUtils.success(null);
    }

    @GetMapping("notification")
    @Operation(summary = "유저 알림 얻기",
            description = "유저 개인 알림을 얻습니다. 저장된 메시지는 (정상적인) 호출 이후 삭제됩니다.<br>" +
                    "=== name 명 리스트(new_badge는 description도 중요) ===<br>" +
                    "1. 시스템 점검 중일 때(단독으로만 반환): system_check<br>" +
                    "2. 앱 버전 업데이트가 필요할 때(단독으로만 반환): update<br>" +
                    "3. 새로운 배지 획득: new_badge(MONTHLY_RANKING_1, MONTHLY_RANKING_2, MONTHLY_RANKING_3, COLLEGE_OF_BUSINESS_ADMINISTRATION_100_AND_FIRST_PLACE)<br>" +
                    "4. 개인 알림: alarm",
            parameters = {
                    @Parameter(name = "version", description = "현재 앱 버전", example = "2.0.2", required = true),
                    @Parameter(name = "os", description = "모바일 운영체제(android 또는 ios)", example = "android")
            }
    )
    public ApiResponse<List<UserNotificationResponse>> getNotification(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                       @RequestParam("version") String appVersion,
                                                                       @RequestParam(name = "os", required = false, defaultValue = "android") String os) {
        OperationSystem operationSystem = OperationSystem.fromString(os.toUpperCase());

        List<UserNotificationResponse> response;
        if (!systemCheckService.isSystemAvailable()) {
            response = UserNotificationResponse.from(NotificationEnum.SYSTEM_CHECK);
        }
        else if (!appVersionService.isSupportedVersion(operationSystem, appVersion)) {
            response = UserNotificationResponse.from(NotificationEnum.UPDATE);
        }
        else {
            List<Notification> notificationList = userProfileService.getNotification(userDetails.getUser());
            response = UserNotificationResponse.from(notificationList);
        }
        return ApiUtils.success(response);
    }

}
