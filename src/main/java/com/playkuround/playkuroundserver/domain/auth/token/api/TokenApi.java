package com.playkuround.playkuroundserver.domain.auth.token.api;

import com.playkuround.playkuroundserver.domain.auth.token.application.RefreshTokenValidator;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenIssueService;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth/tokens")
@RequiredArgsConstructor
public class TokenApi {

    private final RefreshTokenValidator refreshTokenValidator;
    private final TokenIssueService tokenIssueService;

    @PostMapping
    public ApiResponse<TokenDto.AccessTokenDto> accessTokenReissue(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        refreshTokenValidator.validateRefreshToken(authorizationHeader);

        String refreshToken = authorizationHeader.split(" ")[1];
        TokenDto.AccessTokenDto accessTokenDto = tokenIssueService.reissueAccessToken(refreshToken);

        return ApiUtils.success(accessTokenDto);
    }

}
