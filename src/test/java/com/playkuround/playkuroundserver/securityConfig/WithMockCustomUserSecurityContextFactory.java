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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {

    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        User user = new User(annotation.email(), annotation.nickname(), annotation.major(), annotation.role());
        String roleName = user.getRole().toString();
        List<String> role = Arrays.stream(roleName.split(",")).toList();
        UserDetailsImpl userDetails = new UserDetailsImpl(user, role, "");

        Collection<? extends GrantedAuthority> authorities = List.of(user.getRole().toString()).stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", authorities);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
