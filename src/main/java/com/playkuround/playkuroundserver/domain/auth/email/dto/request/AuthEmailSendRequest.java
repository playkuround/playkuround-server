package com.playkuround.playkuroundserver.domain.auth.email.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AuthEmailSendRequest {
    @Email
    private String target;
}
