package com.playkuround.playkuroundserver.domain.user.api;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.api.request.UserRegisterRequest;
import com.playkuround.playkuroundserver.domain.user.api.response.UserRegisterResponse;
import com.playkuround.playkuroundserver.domain.user.application.UserDeleteService;
import com.playkuround.playkuroundserver.domain.user.application.UserLogoutService;
import com.playkuround.playkuroundserver.domain.user.application.UserRegisterService;
import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.domain.user.dto.UserRegisterDto;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserManagementApi {

    private final TokenService tokenService;
    private final UserLogoutService userLogoutService;
    private final UserRegisterService userRegisterService;
    private final UserDeleteService userDeleteService;

    @PostMapping("/register")
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(summary = "회원가입", description = "신규 회원가입 합니다.")
    public ApiResponse<UserRegisterResponse> registerUser(@RequestBody @Valid UserRegisterRequest request) {
        tokenService.validateAuthVerifyToken(request.getAuthVerifyToken());

        UserRegisterDto userRegisterDto
                = new UserRegisterDto(request.getEmail(), request.getNickname(), Major.valueOf(request.getMajor()));
        TokenDto tokenDto = userRegisterService.registerUser(userRegisterDto);

        tokenService.deleteAuthVerifyToken(request.getAuthVerifyToken());
        return ApiUtils.success(UserRegisterResponse.from(tokenDto));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "서버에서 refresh token을 삭제합니다. 앱 내에서 accessToken을 지워야합니다.")
    public ApiResponse<Void> userLogout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userLogoutService.logout(userDetails.getUser());
        return ApiUtils.success(null);
    }

    @DeleteMapping
    @Operation(summary = "회원 탈퇴", description = "회원 정보를 삭제합니다.")
    public ApiResponse<Void> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userDeleteService.deleteUser(userDetails.getUser());
        return ApiUtils.success(null);
    }

}
