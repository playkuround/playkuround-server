package com.playkuround.playkuroundserver.domain.auth.token.api;

import com.playkuround.playkuroundserver.domain.auth.token.api.request.TokenReissueRequest;
import com.playkuround.playkuroundserver.domain.auth.token.api.response.TokenReissueResponse;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenReissueService;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증, 토큰 서비스")
public class TokenApi {

    private final TokenReissueService tokenReissueService;

    @PostMapping("reissue")
    @Operation(summary = "access token 재발급", description = "access token과 refresh token을 재발급받습니다.")
    public ApiResponse<TokenReissueResponse> accessTokenReissue(@RequestBody @Valid TokenReissueRequest request) {
        TokenDto tokenDto = tokenReissueService.reissue(request.getRefreshToken());
        return ApiUtils.success(TokenReissueResponse.from(tokenDto));
    }

}
