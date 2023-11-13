package com.playkuround.playkuroundserver.domain.auth.email.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AuthEmailSendRequest {
    @Email
    @Schema(description = "인증을 진행할 건국대 메일", example = "tester@konkuk.ac.kr", requiredMode = Schema.RequiredMode.REQUIRED)
    private String target;
}
