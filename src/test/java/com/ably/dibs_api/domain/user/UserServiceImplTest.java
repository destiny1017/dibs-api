package com.ably.dibs_api.domain.user;

import com.ably.dibs_api.IntegrationTestSupport;
import com.ably.dibs_api.config.error.BusinessException;
import com.ably.dibs_api.config.error.ErrorCode;
import com.ably.dibs_api.domain.user.dto.LoginServiceRequest;
import com.ably.dibs_api.domain.user.dto.SignUpServiceRequest;
import com.ably.dibs_api.domain.user.dto.UserInfoServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
class UserServiceImplTest extends IntegrationTestSupport {

    UserRepository userRepository;
    UserService userService;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImplTest(UserRepository userRepository, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Test
    @DisplayName("ID로 검색시 일치하는 유저가 반환된다")
    void findUserByIdentifierTest1() {
        // Arrange
        User user1 = createUser("user1@ably.com", "KimBly", "encodedPassword");
        User user2 = createUser("user2@ably.com", "LeeBly", "encodedPassword");
        userRepository.saveAll(List.of(user1, user2));

        // Act
        User targetUser = userRepository.findByEmail("user2@ably.com").get();
        User result = userService.findUserByIdentifier(targetUser.getId());

        // Assert
        assertThat(result).isNotNull()
                .extracting("email", "name", "password")
                .contains("user2@ably.com", "LeeBly", "encodedPassword");

    }

    @Test
    @DisplayName("ID로 검색시 일치하는 유저 없으면 exception 발생")
    void findUserByIdentifierTest2() {
        // Arrange
        User user1 = createUser("user1@ably.com", "KimBly", "encodedPassword");
        userRepository.saveAll(List.of(user1));

        // Act
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.findUserByIdentifier(-1L));

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_ENTITY);
    }

    @Test
    @DisplayName("가입된 이메일 없으면 신규로 유저 생성하여 저장")
    void signupTest1() {
        // Arrange
        User user1 = createUser("user1@ably.com", "KimBly", "encodedPassword");
        User user2 = createUser("user2@ably.com", "LeeBly", "encodedPassword");
        userRepository.saveAll(List.of(user1, user2));

        // Act
        SignUpServiceRequest newUser = SignUpServiceRequest.builder()
                .email("user3@ably.com")
                .password("password")
                .build();
        userService.signup(newUser);
        Optional<User> result = userRepository.findByEmail("user3@ably.com");

        // Assert
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getEmail()).isEqualTo("user3@ably.com");
        assertThat(result.get().getPassword()).isNotEqualTo("password");
    }

    @Test
    @DisplayName("가입하려는 이메일과 동일한 이메일 있으면 exception 발생")
    void singupTest2() {
        // Arrange
        User user1 = createUser("user1@ably.com", "KimBly", "encodedPassword");
        User user2 = createUser("user3@ably.com", "LeeBly", "encodedPassword");
        userRepository.saveAll(List.of(user1, user2));

        // Act
        SignUpServiceRequest newUser = SignUpServiceRequest.builder()
                .email("user3@ably.com")
                .password("password")
                .build();
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.signup(newUser));

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EXIST_EMAIL);

    }

    @Test
    @DisplayName("이메일이 존재하고 비밀번호가 일치하면 유저를 반환한다")
    void loginTest1() {
        // Arrange
        String encodedPassword = bCryptPasswordEncoder.encode("encodedPassword");
        User user1 = createUser("user1@ably.com", "KimBly", encodedPassword);
        userRepository.saveAll(List.of(user1));

        // Act
        LoginServiceRequest loginRequest = LoginServiceRequest.builder()
                .email("user1@ably.com")
                .password("encodedPassword")
                .build();
        User user = userService.login(loginRequest);

        // Assert
        assertThat(user).isNotNull()
                .extracting("email", "name")
                .contains("user1@ably.com", "KimBly");
    }

    @Test
    @DisplayName("이메일이 존재하지 않으면 exception 발생")
    void loginTest2() {
        // Arrange
        User anotherUser = createUser("anotherUser@ably.com", "someone", "pass!");
        userRepository.saveAll(List.of(anotherUser));

        // Act
        LoginServiceRequest loginRequest = LoginServiceRequest.builder()
                .email("user1@ably.com")
                .password("encodedPassword")
                .build();
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.login(loginRequest));

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_ENTITY);
    }

    @Test
    @DisplayName("이메일의 패스워드와 일치하지 않으면 exception 발생")
    void loginTest3() {
        // Arrange
        String encodedPassword = bCryptPasswordEncoder.encode("lawPassword");
        User user1 = createUser("user1@ably.com", "KimBly", encodedPassword);
        userRepository.saveAll(List.of(user1));

        // Act
        LoginServiceRequest loginRequest = LoginServiceRequest.builder()
                .email("user1@ably.com")
                .password("wrongPassword")
                .build();
        // Act
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.login(loginRequest));

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_INCORRECT);
    }

    @Test
    @DisplayName("사용자 정보 조회시 해당 ID가 존재하면 사용자 정보 반환")
    void getUserInfoTest1() {
        // Arrange
        User user1 = createUser("user1@ably.com", "KimBly", "encodedPassword");
        User user2 = createUser("user2@ably.com", "LeeBly", "encodedPassword");
        userRepository.saveAll(List.of(user1, user2));

        // Act
        User targetUser = userRepository.findByEmail("user2@ably.com").get();
        UserInfoServiceResponse result = userService.getUserInfo(targetUser.getId());

        // Assert
        assertThat(result).isNotNull()
                .extracting("email", "name")
                .contains("user2@ably.com", "LeeBly");
    }

    @Test
    @DisplayName("사용자 정보 조회시 해당 ID가 존재하지 않으면 exception 발생")
    void getUserInfoTest2() {
        // Arrange
        User anotherUser = createUser("anotherUser@ably.com", "someone", "pass!");
        userRepository.saveAll(List.of(anotherUser));
        
        // Act
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.getUserInfo(-1L));

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_ENTITY);

    }

    private static User createUser(String email, String name, String password) {
        return User.builder()
                .email(email)
                .name(name)
                .password(password)
                .build();
    }

}