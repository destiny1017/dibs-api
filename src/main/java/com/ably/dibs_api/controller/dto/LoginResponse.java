package com.ably.dibs_api.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private Long id;

    public static LoginResponse of(Long id) {
        return LoginResponse.builder()
                .id(id)
                .build();
    }
}
