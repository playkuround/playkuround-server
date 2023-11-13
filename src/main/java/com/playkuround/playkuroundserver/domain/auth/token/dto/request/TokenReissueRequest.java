package com.playkuround.playkuroundserver.domain.auth.token.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
public class TokenReissueRequest {

    @NotBlank(message = "accessToken은 필수 입력 값입니다.")
    @Schema(description = "Access Token", example = "eyJ0eXAiOiJ..{생략}..")
    private String accessToken;

    @NotBlank(message = "refreshToken은 필수 입력 값입니다.")
    @Schema(description = "Refresh Token", example = "eyJ0aAdJ13..{생략}..")
    private String refreshToken;
}
