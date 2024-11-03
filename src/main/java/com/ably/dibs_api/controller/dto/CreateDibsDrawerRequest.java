package com.ably.dibs_api.controller.dto;

import com.ably.dibs_api.domain.dibs.dto.CreateDibsDrawerServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDibsDrawerRequest implements Serializable {

    @NotEmpty
    private Long userId;

    @NotEmpty
    private String drawerName;

    public static CreateDibsDrawerServiceRequest toServiceRequest(CreateDibsDrawerRequest request) {
        return CreateDibsDrawerServiceRequest.builder()
                .userId(request.getUserId())
                .drawerName(request.getDrawerName())
                .build();
    }
}
