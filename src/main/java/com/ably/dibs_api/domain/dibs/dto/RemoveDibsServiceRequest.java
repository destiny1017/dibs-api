package com.ably.dibs_api.domain.dibs.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RemoveDibsServiceRequest {
    private Long userId;
    private Long productId;
}
