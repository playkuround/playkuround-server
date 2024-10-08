package com.playkuround.playkuroundserver.domain.user.api;

import com.playkuround.playkuroundserver.domain.badge.application.BadgeService;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.user.api.request.ManualBadgeSaveRequest;
import com.playkuround.playkuroundserver.domain.user.application.NewMonthUpdateService;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin API(관리자 권한이 없는 경우 403 에러가 발생합니다.)")
public class AdminApi {

    private final BadgeService badgeService;
    private final NewMonthUpdateService newMonthUpdateService;

    @PostMapping("badges/manual")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "수동 배지 등록",
            description = "수동으로 배지를 등록합니다. 성공적으로 배지 저장에 성공한 개수를 반환합니다. 설정에 따라 개인 메시지로 등록할 수 있습니다.")
    public ApiResponse<Integer> saveManualBadge(@RequestBody @Valid ManualBadgeSaveRequest request) {
        BadgeType badgeType = BadgeType.valueOf(request.getBadge());
        int response = badgeService.saveManualBadge(request.getUserEmail(), badgeType, request.isRegisterMessage());
        return ApiUtils.success(response);
    }

    @PostMapping("new-month-update")
    @Operation(summary = "월(month) 업데이트",
            description = "== 해당 API는 아래의 작업을 포함합니다. ==<br>" +
                    "1. MONTHLY_RANKING_1, MONTHLY_RANKING_2, MONTHLY_RANKING_3 부여(+메시지 부여)<br>" +
                    "2. 랜드마크별 랭킹 초기화<br>" +
                    "3. 유저별 최고 랭킹, 스코어 기록<br>" +
                    "4. 종합 랭킹 초기화")
    public ApiResponse<Void> updateNewMonth() {
        newMonthUpdateService.updateMonth();
        return ApiUtils.success(null);
    }

}
