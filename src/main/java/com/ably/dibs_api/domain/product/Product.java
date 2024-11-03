package com.ably.dibs_api.domain.product;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "PRODUCT")
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "THUMBNAIL")
    private String thumbnail;

    @Column(name = "PRICE")
    private Integer price;

    @Builder
    public Product(Long id, String name, String thumbnail, Integer price) {
        this.id = id;
        this.name = name;
        this.thumbnail = thumbnail;
        this.price = price;
    }
}
