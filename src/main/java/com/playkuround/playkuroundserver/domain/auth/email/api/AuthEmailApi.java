package com.playkuround.playkuroundserver.domain.auth.email.api;

import com.playkuround.playkuroundserver.domain.auth.email.application.AuthEmailSendService;
import com.playkuround.playkuroundserver.domain.auth.email.application.AuthEmailVerifyService;
import com.playkuround.playkuroundserver.domain.auth.email.dto.request.AuthEmailSendRequest;
import com.playkuround.playkuroundserver.domain.auth.email.dto.response.AuthEmailSendResponse;
import com.playkuround.playkuroundserver.domain.auth.email.dto.response.AuthVerifyEmailResponse;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/emails")
@Tag(name = "Auth", description = "메일 인증 서비스")
public class AuthEmailApi {

    private final AuthEmailSendService authEmailSendService;
    private final AuthEmailVerifyService authEmailVerifyService;

    @PostMapping
    public ApiResponse<AuthEmailSendResponse> authEmailSend(
            @RequestBody @Valid AuthEmailSendRequest requestDto) {
        AuthEmailSendResponse responseDto = authEmailSendService.sendAuthEmail(requestDto);
        return ApiUtils.success(responseDto);
    }

    @GetMapping
    public ApiResponse<AuthVerifyEmailResponse> authEmailVerify(@RequestParam("code") String code,
                                                                @RequestParam("email") String email) {
        AuthVerifyEmailResponse result = authEmailVerifyService.verifyAuthEmail(code, email);
        return ApiUtils.success(result);
    }

}
