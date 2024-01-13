package com.playkuround.playkuroundserver.domain.auth.email.api;

import com.playkuround.playkuroundserver.domain.auth.email.application.AuthEmailSendService;
import com.playkuround.playkuroundserver.domain.auth.email.application.AuthEmailVerifyService;
import com.playkuround.playkuroundserver.domain.auth.email.dto.request.AuthEmailSendRequest;
import com.playkuround.playkuroundserver.domain.auth.email.dto.response.AuthEmailSendResponse;
import com.playkuround.playkuroundserver.domain.auth.email.dto.response.AuthVerifyEmailResponse;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/emails")
@Tag(name = "Auth", description = "인증, 토큰 서비스")
public class AuthEmailApi {

    private final AuthEmailSendService authEmailSendService;
    private final AuthEmailVerifyService authEmailVerifyService;

    @PostMapping
    @Operation(summary = "인증메일 전송", description = "해당 메일로 알파벳 대소문자, 숫자 조합으로 이루어진 6자리의 인증 코드를 전송합니다. " +
            "인증 메일 전송은 자정을 기준으로 최대 5번까지 가능합니다. 또한 인증 유효시간은 5분입니다.")
    public ApiResponse<AuthEmailSendResponse> authEmailSend(@RequestBody @Valid AuthEmailSendRequest requestDto) {
        AuthEmailSendResponse response = authEmailSendService.sendAuthEmail(requestDto.getTarget());
        return ApiUtils.success(response);
    }

    @GetMapping
    @Operation(summary = "인증 코드 확인", description = "인증 코드를 확인합니다. 인증 코드는 5분간 유효합니다.")
    public ApiResponse<AuthVerifyEmailResponse> authEmailVerify(@RequestParam("code") String code,
                                                                @RequestParam("email") String email) {
        AuthVerifyEmailResponse response = authEmailVerifyService.verifyAuthEmail(code, email);
        return ApiUtils.success(response);
    }

}
