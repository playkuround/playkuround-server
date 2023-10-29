package com.playkuround.playkuroundserver.domain.user.api;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.user.application.UserLogoutService;
import com.playkuround.playkuroundserver.domain.user.application.UserProfileService;
import com.playkuround.playkuroundserver.domain.user.application.UserRegisterService;
import com.playkuround.playkuroundserver.domain.user.dto.UserProfileDto;
import com.playkuround.playkuroundserver.domain.user.dto.UserRegisterDto;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApi {

    private final UserRegisterService userRegisterService;
    private final UserLogoutService userLogoutService;
    private final UserProfileService userProfileService;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ApiResponse<UserRegisterDto.Response> registerUser(@RequestBody @Valid UserRegisterDto.Request registerRequest) {
        tokenService.validateAuthVerifyToken(registerRequest.getAuthVerifyToken());
        UserRegisterDto.Response registerResponse = userRegisterService.registerUser(registerRequest);
        tokenService.deleteAuthVerifyToken(registerRequest.getAuthVerifyToken());
        return ApiUtils.success(registerResponse);
    }

    @GetMapping
    public ApiResponse<UserProfileDto.Response> userProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserProfileDto.Response profileResponse = userProfileService.getUserProfile(userDetails.getUser());
        return ApiUtils.success(profileResponse);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> userLogout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userLogoutService.logout(userDetails.getUser());
        return ApiUtils.success(null);
    }

    @GetMapping("/duplication")
    public ApiResponse<Boolean> nicknameDuplication(@Param("nickname") String nickname) {
        boolean isDuplicate = userProfileService.checkDuplicateNickname(nickname);
        return ApiUtils.success(isDuplicate);
    }

    @DeleteMapping
    public ApiResponse<Void> userDelete(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userRegisterService.deleteUser(userDetails.getUser());
        return ApiUtils.success(null);
    }

}
