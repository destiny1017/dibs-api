package com.ably.dibs_api.domain.dibs;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DibsDrawerRepository extends JpaRepository<DibsDrawer, Long> {

    Optional<DibsDrawer> findByUserIdAndName(Long userId, String name);

    Page<DibsDrawer> findByUserId(Long userId, Pageable pageable);

    Optional<DibsDrawer> findFirstByUserId(Long userId);

}
