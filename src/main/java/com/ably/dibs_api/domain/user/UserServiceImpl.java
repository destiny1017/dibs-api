package com.ably.dibs_api.domain.user;

import com.ably.dibs_api.config.error.BusinessException;
import com.ably.dibs_api.config.error.ErrorCode;
import com.ably.dibs_api.domain.user.dto.LoginServiceRequest;
import com.ably.dibs_api.domain.user.dto.SignUpServiceRequest;
import com.ably.dibs_api.domain.user.dto.UserInfoServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User findUserByIdentifier(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ENTITY));
    }

    @Override
    @Transactional
    public void signup(SignUpServiceRequest request) {

        // 가입된 이메일 있는지 확인
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if(user.isPresent()) {
            throw new BusinessException(ErrorCode.EXIST_EMAIL);
        }

        // 이메일 없으면 신규가입 진행
        User newUser = User.builder()
                .email(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();
        userRepository.save(newUser);
    }

    @Override
    public User login(LoginServiceRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ENTITY));
        if(bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            return user;
        }
        throw new BusinessException(ErrorCode.PASSWORD_INCORRECT);
    }

    @Override
    public UserInfoServiceResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ENTITY));
        return UserInfoServiceResponse.of(user);
    }

}
