package com.playkuround.playkuroundserver.domain.auth.token.application;

import com.playkuround.playkuroundserver.domain.auth.token.domain.GrantType;
import com.playkuround.playkuroundserver.domain.auth.token.domain.TokenType;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.auth.token.exception.InvalidTokenException;
import com.playkuround.playkuroundserver.global.error.exception.AuthenticationException;
import com.playkuround.playkuroundserver.global.error.ErrorCode;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenManager {

    @Value("${token.access-token-expiration}")
    private String accessTokenExpiration;

    @Value("${token.refresh-token-expiration}")
    private String refreshTokenExpiration;

    @Value("${token.secret}")
    private String tokenSecret;

    public TokenDto createTokenDto(String email) {
        Date accessTokenExpiredAt = createAccessTokenExpirationTime();
        Date refreshTokenExpiredAt = createRefreshTokenExpirationTime();

        String accessToken = createAccessToken(email, accessTokenExpiredAt);
        String refreshToken = createRefreshToken(email, refreshTokenExpiredAt);
        return TokenDto.builder()
                .grantType(GrantType.BEARER.getType())
                .accessToken(accessToken)
                .accessTokenExpiredAt(accessTokenExpiredAt)
                .refreshToken(refreshToken)
                .refreshTokenExpiredAt(refreshTokenExpiredAt)
                .build();
    }

    public Date createAccessTokenExpirationTime() {
        return new Date(System.currentTimeMillis() + Long.parseLong(accessTokenExpiration));
    }

    public Date createRefreshTokenExpirationTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, Integer.parseInt(refreshTokenExpiration));
        return cal.getTime();
    }

    public String createAccessToken(String email, Date expiredAt) {
        return Jwts.builder()
                .setSubject(TokenType.ACCESS.name())
                .setAudience(email)
                .setIssuedAt(new Date())
                .setExpiration(expiredAt)
                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                .setHeaderParam("typ", "JWT")
                .compact();
    }

    public String createRefreshToken(String email, Date expiredAt) {
        return Jwts.builder()
                .setSubject(TokenType.REFRESH.name())
                .setAudience(email)
                .setIssuedAt(new Date())
                .setExpiration(expiredAt)
                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                .setHeaderParam("typ", "JWT")
                .compact();
    }

    public String getUserEmail(String token) {
        String email;
        try {
            Claims claims = Jwts.parser().setSigningKey(tokenSecret)
                    .parseClaimsJws(token).getBody();
            email = claims.getAudience();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidTokenException();
        }
        return email;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(tokenSecret)
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("토큰 기한 만료", e);
            throw new AuthenticationException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException e) {  // 토큰 변조
            log.info("잘못된 jwt token", e);
            throw new AuthenticationException(ErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            log.info("jwt token 검증 중 에러 발생", e);
        }
        return false;
    }

    public String getTokenType(String token) {
        String tokenType;
        try {
            Claims claims = Jwts.parser().setSigningKey(tokenSecret)
                    .parseClaimsJws(token).getBody();
            tokenType = claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidTokenException();
        }

        return tokenType;
    }

}
