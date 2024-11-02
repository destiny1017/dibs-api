package com.ably.dibs_api.controller.dto;

import com.ably.dibs_api.domain.user.dto.LoginServiceRequest;
import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;

    public static LoginServiceRequest toServiceRequest(LoginRequest request) {
        return LoginServiceRequest.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }
}
