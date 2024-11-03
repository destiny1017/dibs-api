package com.ably.dibs_api.domain.dibs;

import com.ably.dibs_api.IntegrationTestSupport;
import com.ably.dibs_api.config.error.BusinessException;
import com.ably.dibs_api.config.error.ErrorCode;
import com.ably.dibs_api.domain.dibs.dto.*;
import com.ably.dibs_api.domain.product.Product;
import com.ably.dibs_api.domain.product.ProductRepository;
import com.ably.dibs_api.domain.user.User;
import com.ably.dibs_api.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
class DibsServiceImplTest extends IntegrationTestSupport {

    private final DibsRepository dibsRepository;
    private final DibsDrawerRepository dibsDrawerRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final DibsService dibsService;

    @Autowired
    public DibsServiceImplTest(DibsRepository dibsRepository, DibsDrawerRepository dibsDrawerRepository, UserRepository userRepository, ProductRepository productRepository, DibsService dibsService) {
        this.dibsRepository = dibsRepository;
        this.dibsDrawerRepository = dibsDrawerRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.dibsService = dibsService;
    }

    @Test
    @DisplayName("해당 사용자의 동일명의 찜 서랍 없으면 생성")
    void createDibsDrawerTest1() {
        // Arrange
        User createdUser = userRepository.save(createUser("user1@ably.com", "KimBly", "encodedPassword"));
        DibsDrawer oldDrawer = createDibsDrawer("old drawer", createdUser);
        dibsDrawerRepository.save(oldDrawer);

        // Act
        CreateDibsDrawerServiceRequest request = CreateDibsDrawerServiceRequest.builder()
                .userId(createdUser.getId())
                .drawerName("new drawer")
                .build();
        CreateDibsDrawerServiceResponse result = dibsService.createDibsDrawer(request);

        // Assert
        assertThat(result).isNotNull()
                .extracting("userId", "drawerName")
                .contains(createdUser.getId(), "new drawer");
    }

    @Test
    @DisplayName("해당 사용자 존재하지 않으면 exception 발생")
    void createDibsDrawerTest2() {
        // Arrange
        // Act
        CreateDibsDrawerServiceRequest request = CreateDibsDrawerServiceRequest.builder()
                .userId(-1L)
                .drawerName("new drawer")
                .build();
        BusinessException exception = assertThrows(BusinessException.class, () -> dibsService.createDibsDrawer(request));

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_ENTITY);
    }

    @Test
    @DisplayName("사용자에게 동일한 이름의 서랍 존재하면 exception 발생")
    void createDibsDrawerTest3() {
        // Arrange
        User createdUser = userRepository.save(createUser("user1@ably.com", "KimBly", "encodedPassword"));
        DibsDrawer oldDrawer = createDibsDrawer("old drawer", createdUser);
        dibsDrawerRepository.save(oldDrawer);

        // Act
        CreateDibsDrawerServiceRequest request = CreateDibsDrawerServiceRequest.builder()
                .userId(createdUser.getId())
                .drawerName("old drawer")
                .build();
        BusinessException exception = assertThrows(BusinessException.class, () -> dibsService.createDibsDrawer(request));

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EXIST_DRAWER_NAME);
    }

    @Test
    @DisplayName("찜 서랍 삭제시 서랍을 포함한 찜 목록도 모두 삭제된다")
    void deleteDibsDrawerTest1() {
        // Arrange
        User createdUser = userRepository.save(createUser("user1@ably.com", "KimBly", "encodedPassword"));
        DibsDrawer createDrawer = dibsDrawerRepository.save(createDibsDrawer("old drawer", createdUser));
        Dibs createDibs = dibsRepository.save(createDibs(createdUser, createDrawer, 1L));

        // Act
        dibsService.deleteDibsDrawer(createDrawer.getId());

        // Assert
        Optional<DibsDrawer> resultDrawer = dibsDrawerRepository.findById(createDrawer.getId());
        Optional<Dibs> resultDibs = dibsRepository.findById(createDibs.getId());
        assertThat(resultDrawer.isPresent()).isFalse();
        assertThat(resultDibs.isPresent()).isFalse();
    }

    @Test
    @DisplayName("찜 서랍 목록을 0페이지로 요청했을 때, 요청한 페이지와 개수대로 응답한다")
    void getMyDrawerListTest1() {
        // Arrange
        User user = userRepository.save(createUser("user1@ably.com", "KimBly", "pass"));
        dibsDrawerRepository.saveAll(
                List.of(createDibsDrawer("drawer1", user),
                        createDibsDrawer("drawer2", user),
                        createDibsDrawer("drawer3", user),
                        createDibsDrawer("drawer4", user),
                        createDibsDrawer("drawer5", user)
                        ));

        // Act
        PageRequest pageable = PageRequest.of(0, 3);
        Page<DibsDrawerResponse> result = dibsService.getMyDrawerList(user.getId(), pageable);

        // Assert
        assertThat(result.getContent()).hasSize(3)
                .extracting("drawerName")
                .contains("drawer1", "drawer2", "drawer3");
    }

    @Test
    @DisplayName("찜 서랍 목록을 1페이지로 요청했을 때, 요청한 페이지와 개수대로 응답한다")
    void getMyDrawerListTest2() {
        // Arrange
        User user = userRepository.save(createUser("user1@ably.com", "KimBly", "pass"));
        dibsDrawerRepository.saveAll(
                List.of(createDibsDrawer("drawer1", user),
                        createDibsDrawer("drawer2", user),
                        createDibsDrawer("drawer3", user),
                        createDibsDrawer("drawer4", user),
                        createDibsDrawer("drawer5", user)
                ));

        // Act
        Page<DibsDrawerResponse> result = dibsService.getMyDrawerList(user.getId(), PageRequest.of(1, 3));

        // Assert
        assertThat(result.getContent()).hasSize(2)
                .extracting("drawerName")
                .contains("drawer4", "drawer5");
    }

    @Test
    @DisplayName("지정한 서랍이 존재하고 해당 상품 찜한적 없으면 찜 저장")
    void addDibsTest1() {
        // Arrange
        User createdUser = userRepository.save(createUser("user1@ably.com", "KimBly", "encodedPassword"));
        DibsDrawer createDrawer = dibsDrawerRepository.save(createDibsDrawer("drawer", createdUser));

        // Act
        AddDibsServiceRequest request = AddDibsServiceRequest.builder()
                .drawerId(createDrawer.getId())
                .userId(createdUser.getId())
                .productId(1L)
                .build();
        Long result = dibsService.addDibs(request);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("서랍 지정X 다른 서랍 존재O 해당 상품 찜기록X 찜 저장")
    void addDibsTest2() {
        // Arrange
        User createdUser = userRepository.save(createUser("user1@ably.com", "KimBly", "encodedPassword"));
        DibsDrawer createDrawer = dibsDrawerRepository.save(createDibsDrawer("drawer", createdUser));

        // Act
        AddDibsServiceRequest request = AddDibsServiceRequest.builder()
                .userId(createdUser.getId())
                .productId(1L)
                .build();
        Long result = dibsService.addDibs(request);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("서랍 지정X 다른 서랍 존재X exception 발생")
    void addDibsTest3() {
        // Arrange
        User createdUser = userRepository.save(createUser("user1@ably.com", "KimBly", "encodedPassword"));

        // Act
        AddDibsServiceRequest request = AddDibsServiceRequest.builder()
                .userId(createdUser.getId())
                .productId(1L)
                .build();
        BusinessException result = assertThrows(BusinessException.class, () -> dibsService.addDibs(request));

        // Assert
        assertThat(result.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_DRAWER);
    }

    @Test
    @DisplayName("해당 상품 찜한적 있으면 exception 발생")
    void addDibsTest4() {
        // Arrange
        User createdUser = userRepository.save(createUser("user1@ably.com", "KimBly", "encodedPassword"));
        DibsDrawer createDrawer1 = dibsDrawerRepository.save(createDibsDrawer("drawer1", createdUser));
        DibsDrawer createDrawer2 = dibsDrawerRepository.save(createDibsDrawer("drawer2", createdUser));
        Dibs createDibs = dibsRepository.save(createDibs(createdUser, createDrawer2, 1L));

        // Act
        AddDibsServiceRequest request = AddDibsServiceRequest.builder()
                .drawerId(createDrawer1.getId())
                .userId(createdUser.getId())
                .productId(1L)
                .build();
        BusinessException result = assertThrows(BusinessException.class, () -> dibsService.addDibs(request));

        // Assert
        assertThat(result.getErrorCode()).isEqualTo(ErrorCode.EXIST_DIBS);
    }

    @Test
    @DisplayName("해당 유저와 해당 상품의 찜 기록이 있으면 삭제한다")
    void removeDibsTest() {
        // Arrange
        User createdUser = userRepository.save(createUser("user1@ably.com", "KimBly", "encodedPassword"));
        DibsDrawer createDrawer1 = dibsDrawerRepository.save(createDibsDrawer("drawer1", createdUser));
        DibsDrawer createDrawer2 = dibsDrawerRepository.save(createDibsDrawer("drawer2", createdUser));
        Dibs createDibs = dibsRepository.save(createDibs(createdUser, createDrawer2, 1L));

        // Act
        RemoveDibsServiceRequest request = RemoveDibsServiceRequest.builder()
                .userId(createdUser.getId())
                .productId(1L)
                .build();
        dibsService.removeDibs(request);
        Optional<Dibs> result = dibsRepository.findById(createDibs.getId());

        // Assert
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    @DisplayName("서랍의 찜 목록을 0페이지로 요청했을 때, 요청한 페이지와 개수대로 응답한다")
    void getDibsListTest1() {
        // Arrange
        User user = userRepository.save(createUser("user1@ably.com", "KimBly", "pass"));
        DibsDrawer drawer = dibsDrawerRepository.save(createDibsDrawer("drawer1", user));
        List<Product> products = productRepository.saveAll(List.of(
                createProduct("product1", "thumb1", 1000),
                createProduct("product2", "thumb2", 5000),
                createProduct("product3", "thumb3", 10000),
                createProduct("product4", "thumb4", 1600),
                createProduct("product5", "thumb5", 2200)
        ));
        List<Dibs> dibs = dibsRepository.saveAll(List.of(
                createDibs(user, drawer, products.get(0).getId()),
                createDibs(user, drawer, products.get(1).getId()),
                createDibs(user, drawer, products.get(2).getId()),
                createDibs(user, drawer, products.get(3).getId()),
                createDibs(user, drawer, products.get(4).getId())
        ));

        // Act
        PageRequest pageable = PageRequest.of(0, 3);
        Page<DibsResponse> result = dibsService.getDibsListByDrawer(drawer.getId(), pageable);

        // Assert
        assertThat(result.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("서랍의 찜 목록을 1페이지로 요청했을 때, 요청한 페이지와 개수대로 응답한다")
    void getDibsListTest2() {
        // Arrange
        User user = userRepository.save(createUser("user1@ably.com", "KimBly", "pass"));
        DibsDrawer drawer = dibsDrawerRepository.save(createDibsDrawer("drawer1", user));
        List<Product> products = productRepository.saveAll(List.of(
                createProduct("product1", "thumb1", 1000),
                createProduct("product2", "thumb2", 5000),
                createProduct("product3", "thumb3", 10000),
                createProduct("product4", "thumb4", 1600),
                createProduct("product5", "thumb5", 2200)
        ));
        List<Dibs> dibs = dibsRepository.saveAll(List.of(
                createDibs(user, drawer, products.get(0).getId()),
                createDibs(user, drawer, products.get(1).getId()),
                createDibs(user, drawer, products.get(2).getId()),
                createDibs(user, drawer, products.get(3).getId()),
                createDibs(user, drawer, products.get(4).getId())
        ));

        // Act
        PageRequest pageable = PageRequest.of(1, 3);
        Page<DibsResponse> result = dibsService.getDibsListByDrawer(drawer.getId(), pageable);

        // Assert
        assertThat(result.getContent()).hasSize(2);
    }

    private static DibsDrawer createDibsDrawer(String name, User user, List<Dibs> dibsList) {
        return DibsDrawer.builder()
                .name(name)
                .user(user)
                .dibsList(dibsList)
                .build();
    }

    private static DibsDrawer createDibsDrawer(String name, User user) {
        return DibsDrawer.builder()
                .name(name)
                .user(user)
                .build();
    }

    private static Product createProduct(String name, String thumbnail, Integer price) {
        return Product.builder()
                .name(name)
                .thumbnail(thumbnail)
                .price(price)
                .build();
    }

    private static Dibs createDibs(User user, DibsDrawer drawer, Long productId) {
        return Dibs.builder()
                .user(user)
                .dibsDrawer(drawer)
                .productId(productId)
                .build();
    }

    private static User createUser(String email, String name, String password) {
        return User.builder()
                .email(email)
                .name(name)
                .password(password)
                .build();
    }


}