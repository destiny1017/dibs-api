package com.ably.dibs_api.domain.user.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "refresh", timeToLive = 1209600)
public class Token {

    @Id
    private Long identifier;

    @Indexed
    private String refreshToken;

    private Collection<? extends GrantedAuthority> authorities;

    public Token(Long identifier, String refreshToken) {
        this.identifier = identifier;
        this.refreshToken = refreshToken;
    }
}