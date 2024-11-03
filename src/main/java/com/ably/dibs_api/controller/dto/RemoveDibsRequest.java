package com.ably.dibs_api.controller.dto;

import com.ably.dibs_api.domain.dibs.dto.RemoveDibsServiceRequest;
import lombok.Builder;
import lombok.Data;

@Data
public class RemoveDibsRequest {
    private Long userId;
    private Long productId;

    @Builder
    public RemoveDibsRequest(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public static RemoveDibsServiceRequest toServiceRequest(RemoveDibsRequest request) {
        return RemoveDibsServiceRequest.builder()
                .userId(request.getUserId())
                .productId(request.getProductId())
                .build();
    }
}
