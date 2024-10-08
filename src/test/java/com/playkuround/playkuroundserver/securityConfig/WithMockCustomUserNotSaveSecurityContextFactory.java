package com.playkuround.playkuroundserver.securityConfig;

import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class WithMockCustomUserNotSaveSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUserNotSave> {

    public SecurityContext createSecurityContext(WithMockCustomUserNotSave annotation) {
        User user = User.create(annotation.email(), annotation.nickname(), annotation.major(), annotation.role());
        String roleName = user.getRole().toString();
        List<String> role = Arrays.stream(roleName.split(",")).toList();
        UserDetailsImpl userDetails = new UserDetailsImpl(user, "", role);

        Collection<? extends GrantedAuthority> authorities = role.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(userDetails, "", authorities);

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);
        return context;
    }
}
