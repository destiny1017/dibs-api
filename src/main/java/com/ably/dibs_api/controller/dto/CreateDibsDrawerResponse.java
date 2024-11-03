package com.ably.dibs_api.controller.dto;

import com.ably.dibs_api.domain.dibs.dto.CreateDibsDrawerServiceResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDibsDrawerResponse implements Serializable {
    private Long userId;
    private Long drawerId;
    private String drawerName;

    public static CreateDibsDrawerResponse of(CreateDibsDrawerServiceResponse response) {
        return CreateDibsDrawerResponse.builder()
                .drawerId(response.getDrawerId())
                .userId(response.getUserId())
                .drawerName(response.getDrawerName())
                .build();
    }
}
