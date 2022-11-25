package com.playkuround.playkuroundserver.domain.user.api;

import com.playkuround.playkuroundserver.domain.user.application.*;
import com.playkuround.playkuroundserver.domain.user.dto.UserLoginDto;
import com.playkuround.playkuroundserver.domain.user.dto.UserProfileDto;
import com.playkuround.playkuroundserver.domain.user.dto.UserRegisterDto;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.resolver.UserEmail;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApi {

    private final UserRegisterService userRegisterService;
    private final UserLoginService userLoginService;
    private final UserLogoutService userLogoutService;
    private final UserProfileService userProfileService;

    @PostMapping("/register")
    public ApiResponse<UserRegisterDto.Response> userRegister(@RequestBody @Valid UserRegisterDto.Request registerRequest) {
        UserRegisterDto.Response registerResponse = userRegisterService.registerUser(registerRequest);
        return ApiUtils.success(registerResponse);
    }

    @PostMapping("/login")
    public ApiResponse<UserLoginDto.Response> userLogin(@UserEmail String userEmail) {
        UserLoginDto.Response loginResponse = userLoginService.login(userEmail);
        return ApiUtils.success(loginResponse);
    }

    @GetMapping
    public ApiResponse<UserProfileDto.Response> userProfile(@UserEmail String userEmail) {
        UserProfileDto.Response profileResponse = userProfileService.getUserProfile(userEmail);
        return ApiUtils.success(profileResponse);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> userLogout(@UserEmail String userEmail) {
        userLogoutService.logout(userEmail);
        return ApiUtils.success(null);
    }

    @GetMapping("/duplication")
    public ApiResponse<Boolean> nicknameDuplication(@Param("nickname") String nickname) {
        boolean isDuplicate = userProfileService.checkDuplicateNickname(nickname);
        return ApiUtils.success(isDuplicate);
    }

    @DeleteMapping
    public ApiResponse<Void> userDelete(@UserEmail String userEmail) {
        userRegisterService.deleteUser(userEmail);
        return ApiUtils.success(null);
    }

}
