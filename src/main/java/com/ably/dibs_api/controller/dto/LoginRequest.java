package com.ably.dibs_api.controller.dto;

import com.ably.dibs_api.domain.user.dto.LoginServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    @Size(max = 100)
    @Pattern(regexp = "^[\\w!#$%&'*+/=?`{|}~^.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank
    @Size(min = 8, max = 16)
    private String password;

    public static LoginServiceRequest toServiceRequest(LoginRequest request) {
        return LoginServiceRequest.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }
}
