package com.ably.dibs_api.controller.dto;

import lombok.Data;

@Data
public class AddDibsResponse {
    private Long id;

    public AddDibsResponse(Long id) {
        this.id = id;
    }
}
