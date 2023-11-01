package com.playkuround.playkuroundserver.domain.user.api;

import com.playkuround.playkuroundserver.domain.user.application.UserProfileService;
import com.playkuround.playkuroundserver.domain.user.dto.UserProfileDto;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileApi {

    private final UserProfileService userProfileService;

    @GetMapping
    public ApiResponse<UserProfileDto.Response> userProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserProfileDto.Response profileResponse = userProfileService.getUserProfile(userDetails.getUser());
        return ApiUtils.success(profileResponse);
    }

    @GetMapping("/availability")
    public ApiResponse<Boolean> nicknameDuplication(@Param("nickname") String nickname) {
        boolean isDuplicate = userProfileService.checkDuplicateNickname(nickname);
        return ApiUtils.success(isDuplicate);
    }

}
