package com.ably.dibs_api.domain.user;

import com.ably.dibs_api.domain.user.dto.LoginServiceRequest;
import com.ably.dibs_api.domain.user.dto.SignUpServiceRequest;
import com.ably.dibs_api.domain.user.dto.UserInfoServiceResponse;

public interface UserService {
    User findUserByIdentifier(Long id);
    void signup(SignUpServiceRequest request);
    User login(LoginServiceRequest request);
    UserInfoServiceResponse getUserInfo(Long userId);
}
