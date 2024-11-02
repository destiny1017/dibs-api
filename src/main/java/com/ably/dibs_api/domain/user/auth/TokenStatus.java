package com.ably.dibs_api.domain.user.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TokenStatus {
    AUTHENTICATED,
    EXPIRED,
    INVALID
}