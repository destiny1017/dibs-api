package com.ably.dibs_api.controller;

import com.ably.dibs_api.config.common.ResponseDto;
import com.ably.dibs_api.controller.dto.LoginRequest;
import com.ably.dibs_api.controller.dto.LoginResponse;
import com.ably.dibs_api.controller.dto.SignUpRequest;
import com.ably.dibs_api.controller.dto.UserInfoResponse;
import com.ably.dibs_api.domain.user.User;
import com.ably.dibs_api.domain.user.UserService;
import com.ably.dibs_api.domain.user.auth.JwtService;
import com.ably.dibs_api.domain.user.dto.UserInfoServiceResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
@Tag(name = "01. 유저 API", description = "유저 및 로그인 API 리스트입니다.")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<Object> signupUser(@RequestBody @Valid SignUpRequest request) {
        userService.signup(SignUpRequest.toServiceRequest(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.ok());
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<LoginResponse>> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        User user = userService.login(LoginRequest.toServiceRequest(request));
        // 로그인 성공시 엑세스 토큰, 리프레시 토큰 쿠키에 세팅
        jwtService.generateAccessToken(response, user);
        jwtService.generateRefreshToken(response, user);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.ok(LoginResponse.of(user.getId())));
    }

    @GetMapping("/info/{userId}")
    public ResponseEntity<ResponseDto<UserInfoResponse>> userInfo(@PathVariable("userId") Long id) {
        UserInfoServiceResponse userInfo = userService.getUserInfo(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.ok(UserInfoResponse.of(userInfo)));
    }
}
