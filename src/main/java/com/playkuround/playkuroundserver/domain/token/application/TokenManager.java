package com.playkuround.playkuroundserver.domain.token.application;

import com.playkuround.playkuroundserver.domain.token.domain.GrantType;
import com.playkuround.playkuroundserver.domain.token.domain.TokenType;
import com.playkuround.playkuroundserver.domain.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.token.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenManager {

    @Value("${token.access-token-expiration-time}")
    private String accessTokenExpirationTime;

    @Value("${token.refresh-token-expiration-time}")
    private String refreshTokenExpirationTime;

    @Value("${token.secret}")
    private String tokenSecret;

    public TokenDto createTokenDto(String email) {
        Date accessTokenExpireTime = createAccessTokenExpirationTime();
        Date refreshTokenExpireTime = createRefreshTokenExpirationTime();

        String accessToken = createAccessToken(email, accessTokenExpireTime);
        String refreshToken = createRefreshToken(email, refreshTokenExpireTime);
        return TokenDto.builder()
                .grantType(GrantType.BEARER.getType())
                .accessToken(accessToken)
                .accessTokenExpireTime(accessTokenExpireTime)
                .refreshToken(refreshToken)
                .refreshTokenExpireTime(refreshTokenExpireTime)
                .build();
    }

    public Date createAccessTokenExpirationTime() {
        return new Date(System.currentTimeMillis() + Long.parseLong(accessTokenExpirationTime));
    }

    public Date createRefreshTokenExpirationTime() {
        return new Date(System.currentTimeMillis() + Long.parseLong(refreshTokenExpirationTime));
    }

    public String createAccessToken(String email, Date expirationTime) {
        String accessToken = Jwts.builder()
                .setSubject(TokenType.ACCESS.name())
                .setAudience(email)
                .setIssuedAt(new Date())
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                .setHeaderParam("typ", "JWT")
                .compact();
        return accessToken;
    }

    public String createRefreshToken(String email, Date expirationTime) {
        String refreshToken = Jwts.builder()
                .setSubject(TokenType.REFRESH.name())
                .setAudience(email)
                .setIssuedAt(new Date())
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                .setHeaderParam("typ", "JWT")
                .compact();
        return refreshToken;
    }

    public String getUserEmail(String accessToken) {
        String email;
        try {
            Claims claims = Jwts.parser().setSigningKey(tokenSecret)
                    .parseClaimsJws(accessToken).getBody();
            email = claims.getAudience();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidTokenException(accessToken);
        }
        return email;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(tokenSecret)
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {  //토큰 변조
            log.info("잘못된 jwt token", e);
        } catch (Exception e) {
            log.info("jwt token 검증 중 에러 발생", e);
        }
        return false;
    }

    public Claims getTokenClaims(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(tokenSecret)
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidTokenException(token);
        }
        return claims;
    }

    public String getTokenType(String token) {
        String tokenType;
        try {
            Claims claims = Jwts.parser().setSigningKey(tokenSecret)
                    .parseClaimsJws(token).getBody();
            tokenType = claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidTokenException(token);
        }

        return tokenType;
    }

    public boolean isTokenExpired(Date tokenExpiredTime) {
        Date now = new Date();
        if (now.after(tokenExpiredTime)) {
            return true;
        }
        return false;
    }

    public String getRole(String token) {
        String role;
        try {
            Claims claims = Jwts.parser().setSigningKey(tokenSecret)
                    .parseClaimsJws(token).getBody();
            role = (String) claims.get("role");
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidTokenException(token);
        }

        return role;
    }

}
