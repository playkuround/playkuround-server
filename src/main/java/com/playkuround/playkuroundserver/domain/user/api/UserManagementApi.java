package com.playkuround.playkuroundserver.domain.user.api;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.user.application.UserLogoutService;
import com.playkuround.playkuroundserver.domain.user.application.UserRegisterService;
import com.playkuround.playkuroundserver.domain.user.dto.UserRegisterDto;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserManagementApi {

    private final TokenService tokenService;
    private final UserLogoutService userLogoutService;
    private final UserRegisterService userRegisterService;

    @PostMapping("/register")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ApiResponse<UserRegisterDto.Response> registerUser(@RequestBody @Valid UserRegisterDto.Request registerRequest) {
        tokenService.validateAuthVerifyToken(registerRequest.getAuthVerifyToken());
        UserRegisterDto.Response registerResponse = userRegisterService.registerUser(registerRequest);
        tokenService.deleteAuthVerifyToken(registerRequest.getAuthVerifyToken());
        return ApiUtils.success(registerResponse);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> userLogout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userLogoutService.logout(userDetails.getUser());
        return ApiUtils.success(null);
    }

    @DeleteMapping
    public ApiResponse<Void> userDelete(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userRegisterService.deleteUser(userDetails.getUser());
        return ApiUtils.success(null);
    }

}
