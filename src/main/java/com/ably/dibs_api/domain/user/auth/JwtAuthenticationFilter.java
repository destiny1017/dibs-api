package com.ably.dibs_api.domain.user.auth;

import com.ably.dibs_api.domain.user.User;
import com.ably.dibs_api.domain.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    public static final String[] LIMITED_URI = {"/dibs", "/dibs-drawer", "/user/info"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 토큰 없이 접근 가능한 URI면 통과
        if (!isLimitedURI(request.getRequestURI())) {
            log.info("Access Public URI");
            filterChain.doFilter(request, response);
            return;
        }

        // 엑세스 토큰 유효하면 통과
        String accessToken = jwtService.resolveTokenFromCookie(request, JwtRule.ACCESS_PREFIX);
        if (jwtService.validateAccessToken(accessToken)) {
            log.info("Valid Access Token");
            filterChain.doFilter(request, response);
            return;
        }

        // 엑세스 토큰 만료, 리프레시 토큰 유효성 체크
        String refreshToken = jwtService.resolveTokenFromCookie(request, JwtRule.REFRESH_PREFIX);
        User user = findUserByRefreshToken(refreshToken);
        if (jwtService.validateRefreshToken(refreshToken, user.getId())) {
            // 액세스 토큰, 리프레시 토큰 모두 갱신
            jwtService.generateAccessToken(response, user);
            jwtService.generateRefreshToken(response, user);
            log.info("Renew Token.. {}", user.getEmail());
            filterChain.doFilter(request, response);
            return;
        }

        jwtService.logout(user, response);
    }

    private boolean isLimitedURI(String requestURI) {
        return Arrays.stream(LIMITED_URI)
                .anyMatch(limited -> requestURI.contains(limited) || limited.contains(requestURI));
    }

    private User findUserByRefreshToken(String refreshToken) {
        String identifier = jwtService.getIdentifierFromRefresh(refreshToken);
        return userService.findUserByIdentifier(Long.parseLong(identifier));
    }

}