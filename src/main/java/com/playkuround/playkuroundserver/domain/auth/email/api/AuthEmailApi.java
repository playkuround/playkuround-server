package com.playkuround.playkuroundserver.domain.auth.email.api;

import com.playkuround.playkuroundserver.domain.auth.email.application.AuthEmailSendService;
import com.playkuround.playkuroundserver.domain.auth.email.application.AuthEmailVerifyService;
import com.playkuround.playkuroundserver.domain.auth.email.dto.AuthEmailSendDto;
import com.playkuround.playkuroundserver.domain.auth.email.dto.AuthVerifyEmailDto;
import com.playkuround.playkuroundserver.domain.auth.email.exception.NotKUEmailException;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/emails")
public class AuthEmailApi {

    private final AuthEmailSendService authEmailSendService;
    private final AuthEmailVerifyService authEmailVerifyService;

    @PostMapping
    public ApiResponse<AuthEmailSendDto.Response> authEmailSend(@RequestBody @Valid AuthEmailSendDto.Request requestDto) {
        AuthEmailSendDto.Response responseDto = authEmailSendService.sendAuthEmail(requestDto);
        return ApiUtils.success(responseDto);
    }

    @GetMapping
    public ApiResponse<AuthVerifyEmailDto.Response> authEmailVerify(@RequestParam("code") String code, @RequestParam("email") String email) {
        AuthVerifyEmailDto.Response result = authEmailVerifyService.verifyAuthEmail(code, email);
        return ApiUtils.success(result);
    }

}
