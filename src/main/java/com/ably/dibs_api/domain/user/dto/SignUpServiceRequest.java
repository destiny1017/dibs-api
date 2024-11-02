package com.ably.dibs_api.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignUpServiceRequest {
    private String email;
    private String password;
    private String name;
}
