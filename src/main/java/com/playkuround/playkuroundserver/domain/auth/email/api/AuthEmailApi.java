package com.playkuround.playkuroundserver.domain.auth.email.api;

import com.playkuround.playkuroundserver.domain.auth.email.application.AuthEmailSendService;
import com.playkuround.playkuroundserver.domain.auth.email.application.AuthEmailVerifyService;
import com.playkuround.playkuroundserver.domain.auth.email.dto.AuthEmailSendDto;
import com.playkuround.playkuroundserver.domain.auth.exception.NotKUEmailException;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/emails")
public class AuthEmailApi {

    private final AuthEmailSendService authEmailSendService;
    private final AuthEmailVerifyService authEmailVerifyService;

    @PostMapping
    public ApiResponse<AuthEmailSendDto.Response> authEmailSend(@RequestBody @Valid AuthEmailSendDto.Request requestDto) {
        if (!requestDto.getTarget().split("@")[1].equals("konkuk.ac.kr")) {
            throw new NotKUEmailException();
        }
        AuthEmailSendDto.Response responseDto = authEmailSendService.sendAuthEmail(requestDto);
        return ApiUtils.success(responseDto);
    }

    @GetMapping
    public ApiResponse<Boolean> authEmailVerify(@RequestParam("code") String code, @RequestParam("email") String email) {
        boolean result = authEmailVerifyService.verifyAuthEmail(code, email);
        return ApiUtils.success(result);
    }

}