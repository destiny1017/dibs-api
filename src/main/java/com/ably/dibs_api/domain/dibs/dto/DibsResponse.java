package com.ably.dibs_api.domain.dibs.dto;

import com.ably.dibs_api.domain.dibs.Dibs;
import lombok.Builder;
import lombok.Data;

@Data
public class DibsResponse {

    private Long dibsId;
    private Long productId;
//    private String productName;
//    private String thumbnail;
//    private Integer price;

//    public DibsResponse(Dibs dibs) {
//        this.dibsId = dibs.getId();
//        this.productId = dibs.getProduct().getId();
//        this.productName = dibs.getProduct().getName();
//        this.thumbnail = dibs.getProduct().getThumbnail();
//        this.price = dibs.getProduct().getPrice();
//    }

    @Builder
    public DibsResponse(Long dibsId, Long productId) {
        this.dibsId = dibsId;
        this.productId = productId;
    }

    public DibsResponse(Dibs dibs) {
        this.dibsId = dibs.getId();
        this.productId = dibs.getProductId();
    }
}
