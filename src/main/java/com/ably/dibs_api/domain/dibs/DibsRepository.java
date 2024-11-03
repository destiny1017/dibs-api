package com.ably.dibs_api.domain.dibs;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DibsRepository extends JpaRepository<Dibs, Long> {
    void deleteAllByDibsDrawerId(Long drawerId);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    Optional<Dibs> findByUserIdAndProductId(Long userId, Long productId);
    Page<Dibs> findByDibsDrawerId(Long dibsDrawerId, Pageable pageable);

//    @Query("SELECT new com.ably.dibs_api.domain.dibs.dto.DibsResponse(d.id, p.id, p.name, p.thumbnail, p.price) " +
//    "FROM DIBS d LEFT JOIN d.productId p " +
//    "WHERE d.dibsDrawerId = :dibsDrawerId")
//    Page<Dibs> findByDibsDrawerId(@Param("dibsDrawerId") Long dibsDrawerId, Pageable pageable);

//    @Query("SELECT D FROM DIBS D " +
//            "JOIN FETCH PRODUCT P " +
//            "WHERE D.DRAWER_ID = :drawerId")
//    Page<Dibs> findByDrawerId(@Param("drawerId") Long drawerId, Pageable pageable);
}
