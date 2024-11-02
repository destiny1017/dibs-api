package com.ably.dibs_api.controller.dto;

import com.ably.dibs_api.domain.user.dto.SignUpServiceRequest;
import lombok.Data;

@Data
public class SignUpRequest {

    private String email;
    private String password;
    private String name;

    public static SignUpServiceRequest toServiceRequest(SignUpRequest request) {
        return SignUpServiceRequest.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .build();
    }
}
