package com.playkuround.playkuroundserver.domain.user.api;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.user.application.UserLogoutService;
import com.playkuround.playkuroundserver.domain.user.application.UserRegisterService;
import com.playkuround.playkuroundserver.domain.user.dto.request.UserRegisterRequest;
import com.playkuround.playkuroundserver.domain.user.dto.response.UserRegisterResponse;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserManagementApi {

    private final TokenService tokenService;
    private final UserLogoutService userLogoutService;
    private final UserRegisterService userRegisterService;

    @PostMapping("/register")
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(summary = "회원가입", description = "회원가입을 진행한다.")
    public ApiResponse<UserRegisterResponse> registerUser(@RequestBody @Valid UserRegisterRequest registerRequest) {
        tokenService.validateAuthVerifyToken(registerRequest.getAuthVerifyToken());
        UserRegisterResponse registerResponse = userRegisterService.registerUser(registerRequest);
        tokenService.deleteAuthVerifyToken(registerRequest.getAuthVerifyToken());
        return ApiUtils.success(registerResponse);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "서버에서 refresh token을 삭제합니다. 앱 내에서 accessToken을 지워야합니다.")
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
