package com.ably.dibs_api.domain.user.dto;

import com.ably.dibs_api.domain.user.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserInfoServiceResponse {
    private String email;
    private String name;
    private LocalDateTime joinDate;

    public static UserInfoServiceResponse of(User user) {
        return UserInfoServiceResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .joinDate(user.getCreatedAt())
                .build();
    }
}
