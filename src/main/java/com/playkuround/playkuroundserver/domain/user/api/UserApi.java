package com.playkuround.playkuroundserver.domain.user.api;

import com.playkuround.playkuroundserver.domain.user.application.UserLoginService;
import com.playkuround.playkuroundserver.domain.user.application.UserLogoutService;
import com.playkuround.playkuroundserver.domain.user.application.UserProfileService;
import com.playkuround.playkuroundserver.domain.user.application.UserRegisterService;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.UserLoginDto;
import com.playkuround.playkuroundserver.domain.user.dto.UserProfileDto;
import com.playkuround.playkuroundserver.domain.user.dto.UserRegisterDto;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.resolver.UserEntity;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApi {

    private final UserRegisterService userRegisterService;
    private final UserLoginService userLoginService;
    private final UserLogoutService userLogoutService;
    private final UserProfileService userProfileService;

    @PostMapping("/register")
    public ApiResponse<UserRegisterDto.Response> userRegister(
            @RequestBody @Valid UserRegisterDto.Request registerRequest) {
        UserRegisterDto.Response registerResponse = userRegisterService.registerUser(registerRequest);
        return ApiUtils.success(registerResponse);
    }

    @PostMapping("/login")
    public ApiResponse<UserLoginDto.Response> userLogin(@UserEntity User user) {
        UserLoginDto.Response loginResponse = userLoginService.login(user);
        return ApiUtils.success(loginResponse);
    }

    @GetMapping
    public ApiResponse<UserProfileDto.Response> userProfile(@UserEntity User user) {
        UserProfileDto.Response profileResponse = userProfileService.getUserProfile(user);
        return ApiUtils.success(profileResponse);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> userLogout(@UserEntity User user) {
        userLogoutService.logout(user);
        return ApiUtils.success(null);
    }

    @GetMapping("/duplication")
    public ApiResponse<Boolean> nicknameDuplication(@Param("nickname") String nickname) {
        boolean isDuplicate = userProfileService.checkDuplicateNickname(nickname);
        return ApiUtils.success(isDuplicate);
    }

    @DeleteMapping
    public ApiResponse<Void> userDelete(@UserEntity User user) {
        userRegisterService.deleteUser(user);
        return ApiUtils.success(null);
    }

}
