package com.ably.dibs_api.domain.user.auth;

import com.ably.dibs_api.config.error.BusinessException;
import com.ably.dibs_api.config.error.ErrorCode;
import com.ably.dibs_api.domain.user.User;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Optional;

import static com.ably.dibs_api.domain.user.auth.JwtRule.*;

@Service
@Transactional(readOnly = true)
@Slf4j
public class JwtService {

    private final JwtGenerator jwtGenerator;
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    private final Key ACCESS_SECRET_KEY;
    private final Key REFRESH_SECRET_KEY;
    private final long ACCESS_EXPIRATION;
    private final long REFRESH_EXPIRATION;

    public JwtService(JwtGenerator jwtGenerator,
                      JwtUtil jwtUtil, TokenRepository tokenRepository,
                      @Value("${jwt.access-secret}") String ACCESS_SECRET_KEY,
                      @Value("${jwt.refresh-secret}") String REFRESH_SECRET_KEY,
                      @Value("${jwt.access-expiration}") long ACCESS_EXPIRATION,
                      @Value("${jwt.refresh-expiration}") long REFRESH_EXPIRATION) {
        this.jwtGenerator = jwtGenerator;
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
        this.ACCESS_SECRET_KEY = jwtUtil.getSigningKey(ACCESS_SECRET_KEY);
        this.REFRESH_SECRET_KEY = jwtUtil.getSigningKey(REFRESH_SECRET_KEY);
        this.ACCESS_EXPIRATION = ACCESS_EXPIRATION;
        this.REFRESH_EXPIRATION = REFRESH_EXPIRATION;
    }


    public String generateAccessToken(HttpServletResponse response, User requestUser) {
        String accessToken = jwtGenerator.generateAccessToken(ACCESS_SECRET_KEY, ACCESS_EXPIRATION, requestUser);
        ResponseCookie cookie = setTokenToCookie(ACCESS_PREFIX.getValue(), accessToken, ACCESS_EXPIRATION / 1000);
        response.addHeader(JWT_ISSUE_HEADER.getValue(), cookie.toString());

        return accessToken;
    }

    @Transactional
    public String generateRefreshToken(HttpServletResponse response, User requestUser) {
        String refreshToken = jwtGenerator.generateRefreshToken(REFRESH_SECRET_KEY, REFRESH_EXPIRATION, requestUser);
        ResponseCookie cookie = setTokenToCookie(REFRESH_PREFIX.getValue(), refreshToken, REFRESH_EXPIRATION / 1000);
        response.addHeader(JWT_ISSUE_HEADER.getValue(), cookie.toString());

        tokenRepository.save(new Token(requestUser.getId(), refreshToken));
        return refreshToken;
    }

    private ResponseCookie setTokenToCookie(String tokenPrefix, String token, long maxAgeSeconds) {
        return ResponseCookie.from(tokenPrefix, token)
                .path("/")
                .maxAge(maxAgeSeconds)
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .build();
    }

    public boolean validateAccessToken(String token) {
        return jwtUtil.getTokenStatus(token, ACCESS_SECRET_KEY) == TokenStatus.AUTHENTICATED;
    }

    public boolean validateRefreshToken(String token, Long id) {
        boolean isRefreshValid = jwtUtil.getTokenStatus(token, REFRESH_SECRET_KEY) == TokenStatus.AUTHENTICATED;

        Optional<Token> storedToken = tokenRepository.findById(id);
        if(storedToken.isPresent()) {
            boolean isTokenMatched = storedToken.get().getRefreshToken().equals(token);
            return isRefreshValid && isTokenMatched;
        }
        return false; // refresh 토큰 미존재
    }

    public String resolveTokenFromCookie(HttpServletRequest request, JwtRule tokenPrefix) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new BusinessException(ErrorCode.JWT_TOKEN_NOT_FOUND);
        }
        return jwtUtil.resolveTokenFromCookie(cookies, tokenPrefix);
    }

    public String getIdentifierFromRefresh(String refreshToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(REFRESH_SECRET_KEY)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_JWT);
        }
    }

    public void logout(User requestUser, HttpServletResponse response) {
        tokenRepository.deleteById(requestUser.getId());

        Cookie accessCookie = jwtUtil.resetToken(ACCESS_PREFIX);
        Cookie refreshCookie = jwtUtil.resetToken(REFRESH_PREFIX);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }
}