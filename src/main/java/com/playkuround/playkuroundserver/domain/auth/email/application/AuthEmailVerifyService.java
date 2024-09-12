package com.playkuround.playkuroundserver.domain.auth.email.application;

import com.playkuround.playkuroundserver.domain.auth.email.dto.AuthVerifyEmailResult;

public interface AuthEmailVerifyService {

    AuthVerifyEmailResult verifyAuthEmail(String code, String email);

}
