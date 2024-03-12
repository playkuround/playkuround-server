package com.playkuround.playkuroundserver.domain.auth.email.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AuthEmailSendRequest {

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수값입니다.")
    @Schema(description = "인증을 진행할 건국대 메일", example = "tester@konkuk.ac.kr", requiredMode = Schema.RequiredMode.REQUIRED)
    private String target;
}
