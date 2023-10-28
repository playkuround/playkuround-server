package com.playkuround.playkuroundserver.domain.auth.token.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
public class TokenReissueRequest {

    @NotBlank(message = "accessToken은 필수 입력 값입니다.")
    private String accessToken;

    @NotBlank(message = "refreshToken은 필수 입력 값입니다.")
    private String refreshToken;
}
