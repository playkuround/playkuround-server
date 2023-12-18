package com.playkuround.playkuroundserver.domain.auth.token.application;

import com.playkuround.playkuroundserver.domain.auth.token.domain.AuthVerifyToken;
import com.playkuround.playkuroundserver.domain.auth.token.domain.GrantType;
import com.playkuround.playkuroundserver.domain.auth.token.domain.RefreshToken;
import com.playkuround.playkuroundserver.domain.auth.token.domain.TokenType;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.auth.token.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenManager {

    private final Key key;
    private final String issuer;
    private final String tokenTypeHeaderKey;
    private final Long accessTokenValidityInMilliseconds;
    private final Long refreshTokenValidityInMilliseconds;
    private final Long authVerifyTokenValidityInSeconds;
    private final UserDetailsService userDetailsService;

    public TokenManager(@Value("${token.secret}") String secretKey,
                        @Value("${token.issuer}") String issuer,
                        @Value("${token.access-token-expiration-seconds}") Long accessTokenExpirationSeconds,
                        @Value("${token.refresh-token-expiration-seconds}") Long refreshTokenExpirationSeconds,
                        @Value("${token.authverify-token-expiration-seconds}") Long authVerifyTokenExpirationSeconds,
                        UserDetailsService userDetailsService) {
        this.accessTokenValidityInMilliseconds = accessTokenExpirationSeconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenExpirationSeconds * 1000;
        this.authVerifyTokenValidityInSeconds = authVerifyTokenExpirationSeconds;
        this.issuer = issuer;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.tokenTypeHeaderKey = "tokentype";
        this.userDetailsService = userDetailsService;
    }

    public TokenDto createTokenDto(Authentication authentication) {
        long now = new Date().getTime();
        Date accessTokenExpiredAt = createAccessTokenExpirationTime(now);
        Date refreshTokenExpiredAt = createRefreshTokenExpirationTime(now);

        String accessToken = createAccessToken(authentication, accessTokenExpiredAt);
        String refreshToken = createRefreshToken(refreshTokenExpiredAt);

        return TokenDto.builder()
                .grantType(GrantType.BEARER.getType())
                .accessToken(accessToken)
                .accessTokenExpiredAt(accessTokenExpiredAt)
                .refreshToken(refreshToken)
                .refreshTokenExpiredAt(refreshTokenExpiredAt)
                .build();
    }

    private Date createAccessTokenExpirationTime(long now) {
        return new Date(now + accessTokenValidityInMilliseconds);
    }

    private Date createRefreshTokenExpirationTime(long now) {
        return new Date(now + refreshTokenValidityInMilliseconds);
    }

    private String createAccessToken(Authentication authentication, Date expireDate) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .claim("auth", authorities)
                .setIssuer(issuer)
                .setExpiration(expireDate)
                .setSubject(authentication.getName())
                .signWith(key, SignatureAlgorithm.HS256)
                .setHeaderParam(tokenTypeHeaderKey, TokenType.ACCESS.name())
                .compact();
    }

    private String createRefreshToken(Date expireDate) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(issuer)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .setHeaderParam(tokenTypeHeaderKey, TokenType.REFRESH.name())
                .compact();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public boolean isValidateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    public String getTokenType(String token) {
        try {
            return (String) Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getHeader()
                    .get(tokenTypeHeaderKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidTokenException();
        }
    }

    public AuthVerifyToken createAuthVerifyToken() {
        String key = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        return new AuthVerifyToken(key, now.plusSeconds(authVerifyTokenValidityInSeconds));
    }

    public RefreshToken createRefreshToken(Authentication authentication, String refreshToken) {
        LocalDateTime now = LocalDateTime.now();
        return RefreshToken.builder()
                .userEmail(authentication.getName())
                .refreshToken(refreshToken)
                .expiredAt(now.plusSeconds(refreshTokenValidityInMilliseconds / 1000))
                .build();
    }
}
