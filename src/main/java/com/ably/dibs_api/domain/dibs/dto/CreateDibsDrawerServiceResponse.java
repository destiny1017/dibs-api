package com.ably.dibs_api.domain.dibs.dto;

import com.ably.dibs_api.domain.dibs.DibsDrawer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateDibsDrawerServiceResponse {
    private Long userId;
    private Long drawerId;
    private String drawerName;

    public static CreateDibsDrawerServiceResponse of(DibsDrawer dibsDrawer) {
        return CreateDibsDrawerServiceResponse.builder()
                .drawerId(dibsDrawer.getId())
                .userId(dibsDrawer.getUser().getId())
                .drawerName(dibsDrawer.getName())
                .build();
    }
}
