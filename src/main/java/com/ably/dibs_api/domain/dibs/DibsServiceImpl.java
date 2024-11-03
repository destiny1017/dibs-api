package com.ably.dibs_api.domain.dibs;

import com.ably.dibs_api.config.error.BusinessException;
import com.ably.dibs_api.config.error.ErrorCode;
import com.ably.dibs_api.domain.dibs.dto.*;
import com.ably.dibs_api.domain.user.User;
import com.ably.dibs_api.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DibsServiceImpl implements DibsService {

    private final DibsRepository dibsRepository;
    private final DibsDrawerRepository dibsDrawerRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateDibsDrawerServiceResponse createDibsDrawer(CreateDibsDrawerServiceRequest request) {
        // 존재하는 사용자인지 확인
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ENTITY));

        // 동일 이름의 찜 서랍 존재여부 확인
        Optional<DibsDrawer> drawer = dibsDrawerRepository.findByUserIdAndName(user.getId(), request.getDrawerName());
        if(drawer.isPresent()) {
            throw new BusinessException(ErrorCode.EXIST_DRAWER_NAME);
        }

        // 생성 및 반환
        return CreateDibsDrawerServiceResponse.of(
                dibsDrawerRepository.save(DibsDrawer.builder()
                .user(user)
                .name(request.getDrawerName())
                .build()));
    }

    @Transactional
    public void deleteDibsDrawer(Long drawerId) {
        // 찜 서랍과 서랍의 찜 목록 전체 삭제
        dibsRepository.deleteAllByDibsDrawerId(drawerId);
        dibsDrawerRepository.deleteById(drawerId);
    }

    public Page<DibsDrawerResponse> getMyDrawerList(Long userId, Pageable pageable) {
        return dibsDrawerRepository.findByUserId(userId, pageable)
                .map(DibsDrawerResponse::new);
    }

    @Transactional
    public Long addDibs(AddDibsServiceRequest request) {
        Optional<DibsDrawer> findDrawer;
        // 찜서랍 지정하지 않았으면 최상단의 찜서랍 가져오기
        if(request.getDrawerId() == null) {
            findDrawer = dibsDrawerRepository.findFirstByUserId(request.getUserId());
        } else {
            findDrawer = dibsDrawerRepository.findById(request.getDrawerId());
        }
        DibsDrawer targetDrawer = findDrawer
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_DRAWER));

        // 해당 유저가 찜한적 있는 상품인지 확인
        boolean dibsExist = dibsRepository.existsByUserIdAndProductId(request.getUserId(), request.getProductId());
        if(dibsExist) {
            throw new BusinessException(ErrorCode.EXIST_DIBS);
        }

        // 저장
        Dibs dibs = dibsRepository.save(Dibs.builder()
                .dibsDrawer(targetDrawer)
                .user(targetDrawer.getUser())
                .productId(request.getProductId())
                .build());
        return dibs.getId();
    }

    @Transactional
    public void removeDibs(RemoveDibsServiceRequest request) {
        // 있으면 삭제. 없다고 굳이 exception 안 일으켜도 될듯
        dibsRepository.findByUserIdAndProductId(request.getUserId(), request.getProductId())
                .ifPresent(dibsRepository::delete);
    }

    @Override
    public Page<DibsResponse> getDibsListByDrawer(Long drawerId, Pageable pageable) {
        return dibsRepository.findByDibsDrawerId(drawerId, pageable)
                .map(DibsResponse::new);
    }


}
