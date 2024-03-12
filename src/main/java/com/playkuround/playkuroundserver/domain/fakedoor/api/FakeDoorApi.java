package com.playkuround.playkuroundserver.domain.fakedoor.api;

import com.playkuround.playkuroundserver.domain.fakedoor.application.FakeDoorService;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fake-door")
@RequiredArgsConstructor
@Tag(name = "fakeDoor API", description = "광고보고 쿠라운드 응원하기 버튼 클릭 API")
public class FakeDoorApi {

    private final FakeDoorService fakeDoorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "광고보고 쿠라운드 응원하기 버튼 클릭", description = "광고보고 쿠라운드 응원하기 버튼 클릭시 호출되는 API")
    public ApiResponse<Void> saveFakeDoor(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        fakeDoorService.saveFakeDoor(userDetails.getUser());
        return ApiUtils.success(null);
    }
}
