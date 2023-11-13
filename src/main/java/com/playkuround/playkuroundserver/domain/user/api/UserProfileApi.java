package com.playkuround.playkuroundserver.domain.user.api;

import com.playkuround.playkuroundserver.domain.user.application.UserProfileService;
import com.playkuround.playkuroundserver.domain.user.dto.response.UserProfileResponse;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserProfileApi {

    private final UserProfileService userProfileService;

    @GetMapping
    @Operation(summary = "프로필 얻기", description = "로그인 유저의 기본 정보를 얻습니다.")
    public ApiResponse<UserProfileResponse> userProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserProfileResponse profileResponse = userProfileService.getUserProfile(userDetails.getUser());
        return ApiUtils.success(profileResponse);
    }

    @GetMapping("/availability")
    @Operation(summary = "해당 닉네임이 사용 가능한지 체크", description = "사용 가능하다면 true가 반환됩니다.")
    public ApiResponse<Boolean> nicknameDuplication(@Param("nickname") String nickname) {
        boolean isDuplicate = userProfileService.checkDuplicateNickname(nickname);
        return ApiUtils.success(!isDuplicate);
    }

}
