package com.ably.dibs_api.controller.dto;

import com.ably.dibs_api.domain.dibs.dto.AddDibsServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddDibsRequest {

    private Long drawerId;

    @NotNull
    private Long userId;

    @NotNull
    private Long productId;

    @Builder
    public AddDibsRequest(Long drawerId, Long userId, Long productId) {
        this.drawerId = drawerId;
        this.userId = userId;
        this.productId = productId;
    }

    public static AddDibsServiceRequest toServiceRequest(AddDibsRequest request) {
        return AddDibsServiceRequest.builder()
                .drawerId(request.getDrawerId())
                .userId(request.getUserId())
                .productId(request.getProductId())
                .build();
    }
}
