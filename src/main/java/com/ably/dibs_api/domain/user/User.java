package com.ably.dibs_api.domain.user;

import com.ably.dibs_api.config.common.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "USER_INFO")
@NoArgsConstructor
public class User extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    private String email;
    private String password;
    private String name;

    @Builder
    public User(Long id, String email, String password, String name) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
    }
}
