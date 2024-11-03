package com.ably.dibs_api.domain.dibs;

import com.ably.dibs_api.config.common.BaseTimeEntity;
import com.ably.dibs_api.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "DIBS_DRAWER")
@NoArgsConstructor
public class DibsDrawer extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "NAME")
    private String name;

    @OneToMany(mappedBy = "dibsDrawer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dibs> dibsList = new ArrayList<>();

    @Builder
    public DibsDrawer(Long id, User user, String name, List<Dibs> dibsList) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.dibsList = dibsList;
    }
}
