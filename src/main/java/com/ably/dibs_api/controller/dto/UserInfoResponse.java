package com.ably.dibs_api.controller.dto;

import com.ably.dibs_api.domain.user.dto.UserInfoServiceResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserInfoResponse {
    private String email;
    private String name;
    private LocalDateTime joinDate;

    public static UserInfoResponse of(UserInfoServiceResponse response) {
        return UserInfoResponse.builder()
                .email(response.getEmail())
                .name(response.getName())
                .joinDate(response.getJoinDate())
                .build();
    }
}
