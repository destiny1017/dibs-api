package com.ably.dibs_api.controller;

import com.ably.dibs_api.config.common.ResponseDto;
import com.ably.dibs_api.controller.dto.LoginRequest;
import com.ably.dibs_api.controller.dto.SignUpRequest;
import com.ably.dibs_api.controller.dto.UserInfoResponse;
import com.ably.dibs_api.domain.user.User;
import com.ably.dibs_api.domain.user.UserService;
import com.ably.dibs_api.domain.user.auth.JwtService;
import com.ably.dibs_api.domain.user.dto.UserInfoServiceResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<Object> signupUser(@RequestBody SignUpRequest request) {
        userService.signup(SignUpRequest.toServiceRequest(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.builder()
                        .code("SUCCESS")
                        .message("회원가입 완료")
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        User user = userService.login(LoginRequest.toServiceRequest(request));
        // 로그인 성공시 엑세스 토큰, 리프레시 토큰 쿠키에 세팅
        jwtService.generateAccessToken(response, user);
        jwtService.generateRefreshToken(response, user);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.builder()
                        .code("SUCCESS")
                        .message("로그인 성공")
                        .build());
    }

    @GetMapping("/info/{userId}")
    public ResponseEntity<Object> userInfo(@PathVariable("userId") Long id) {
        UserInfoServiceResponse userInfo = userService.getUserInfo(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.builder()
                        .code("SUCCESS")
                        .data(UserInfoResponse.of(userInfo))
                        .build());
    }
}
