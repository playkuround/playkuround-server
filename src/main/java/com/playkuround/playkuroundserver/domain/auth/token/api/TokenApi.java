package com.playkuround.playkuroundserver.domain.auth.token.api;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenReissueService;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenReissueRequest;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenReissueResponse;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TokenApi {

    private final TokenReissueService tokenReissueService;

    @PostMapping("reissue")
    public ApiResponse<TokenReissueResponse> accessTokenReissue(@RequestBody @Valid TokenReissueRequest request) {
        TokenReissueResponse response = tokenReissueService.reissue(request);
        return ApiUtils.success(response);
    }

}
