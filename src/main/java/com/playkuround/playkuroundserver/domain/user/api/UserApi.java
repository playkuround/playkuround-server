package com.playkuround.playkuroundserver.domain.user.api;

import com.playkuround.playkuroundserver.domain.user.application.*;
import com.playkuround.playkuroundserver.domain.user.dto.UserLoginDto;
import com.playkuround.playkuroundserver.domain.user.dto.UserProfileDto;
import com.playkuround.playkuroundserver.domain.user.dto.UserRegisterDto;
import com.playkuround.playkuroundserver.global.common.response.ApiResult;
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
    public ApiResult<UserRegisterDto.Response> UserRegister(@RequestBody @Valid UserRegisterDto.Request registerRequest) {
        UserRegisterDto.Response registerResponse = userRegisterService.registerUser(registerRequest);
        return ApiUtils.success(registerResponse);
    }

    @PostMapping("/login")
    public ApiResult<UserLoginDto.Response> UserLogin(@UserEmail String userEmail) {
        UserLoginDto.Response loginResponse = userLoginService.login(userEmail);
        return ApiUtils.success(loginResponse);
    }

    @GetMapping
    public ApiResult<UserProfileDto.Response> UserProfile(@UserEmail String userEmail) {
        UserProfileDto.Response profileResponse = userProfileService.getUserProfile(userEmail);
        return ApiUtils.success(profileResponse);
    }

    @PostMapping("/logout")
    public ApiResult<Void> UserLogout(@UserEmail String userEmail) {
        userLogoutService.logout(userEmail);
        return ApiUtils.success(null);
    }

    @GetMapping("/duplication")
    public ApiResult<Boolean> nicknameDuplication(@Param("nickname") String nickname) {
        boolean isDuplicate = userProfileService.checkDuplicateNickname(nickname);
        return ApiUtils.success(isDuplicate);
    }

}
