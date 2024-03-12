package com.playkuround.playkuroundserver.global.security;

import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.exception.UserNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final String ENCODED_PASSWORD;
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.ENCODED_PASSWORD = passwordEncoder.encode("");
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(UserNotFoundException::new);

        String roleName = user.getRole().toString();
        List<String> role = Arrays.stream(roleName.split(",")).toList();
        return new UserDetailsImpl(user, ENCODED_PASSWORD, role);
    }
}
