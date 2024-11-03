package com.ably.dibs_api.domain.dibs;

import com.ably.dibs_api.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "DIBS")
@NoArgsConstructor
public class Dibs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DRAWER_ID")
    private DibsDrawer dibsDrawer;

    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Builder
    public Dibs(Long id, User user, DibsDrawer dibsDrawer, Long productId) {
        this.id = id;
        this.user = user;
        this.dibsDrawer = dibsDrawer;
        this.productId = productId;
    }
}
