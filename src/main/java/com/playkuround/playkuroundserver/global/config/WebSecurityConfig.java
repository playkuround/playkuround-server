package com.playkuround.playkuroundserver.global.config;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenManager;
import com.playkuround.playkuroundserver.global.security.JwtAuthenticationFilter;
import com.playkuround.playkuroundserver.global.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final TokenManager tokenManager;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                //PathRequest.toH2Console(),
                                PathRequest.toStaticResources().atCommonLocations(),
                                AntPathRequestMatcher.antMatcher("/"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/users/register"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/users/login"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/users/availability"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/tokens"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/auth/emails"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/emails"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/reissue"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/health"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/swagger-ui/**"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/swagger-ui.html"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api-docs/**"),
                                AntPathRequestMatcher.antMatcher("/actu/**")
                        ).permitAll()
                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher("/api/admin/**")
                        ).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(tokenManager), UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(userDetailsService)
                .build();
    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        configuration.setAllowCredentials(true);
//        configuration.setAllowedOriginPatterns(List.of("*"));
//        configuration.setAllowedMethods(List.of("HEAD", "POST", "GET", "DELETE", "PUT"));
//        configuration.setAllowedHeaders(List.of("*"));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
}

