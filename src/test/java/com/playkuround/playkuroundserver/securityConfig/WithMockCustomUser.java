package com.playkuround.playkuroundserver.securityConfig;


import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.domain.user.domain.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    String email() default "tester@konkuk.ac.kr";

    String nickname() default "tester";

    Major major() default Major.컴퓨터공학부;

    Role role() default Role.ROLE_USER;
}
