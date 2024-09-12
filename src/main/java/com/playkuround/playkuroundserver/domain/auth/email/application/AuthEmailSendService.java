package com.playkuround.playkuroundserver.domain.auth.email.application;

import com.playkuround.playkuroundserver.domain.auth.email.dto.AuthEmailInfo;

public interface AuthEmailSendService {

    AuthEmailInfo sendAuthEmail(String target);

}
