package com.ably.dibs_api.domain.dibs.dto;

import com.ably.dibs_api.domain.dibs.DibsDrawer;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DibsDrawerResponse {
    private Long id;
    private String drawerName;
    private LocalDateTime createdAt;
//    private Long dibsCount; 추후 작성

    public DibsDrawerResponse(DibsDrawer drawer) {
        this.id = drawer.getId();
        this.drawerName = drawer.getName();
        this.createdAt = drawer.getCreatedAt();
    }

    @Builder
    public DibsDrawerResponse(Long id, String drawerName, LocalDateTime createdAt) {
        this.id = id;
        this.drawerName = drawerName;
        this.createdAt = createdAt;
    }
}
