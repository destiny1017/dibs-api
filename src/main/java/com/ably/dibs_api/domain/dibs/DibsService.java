package com.ably.dibs_api.domain.dibs;

import com.ably.dibs_api.domain.dibs.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DibsService {
    CreateDibsDrawerServiceResponse createDibsDrawer(CreateDibsDrawerServiceRequest request);
    void deleteDibsDrawer(Long drawerId);
    Page<DibsDrawerResponse> getMyDrawerList(Long userId, Pageable pageable);
    Long addDibs(AddDibsServiceRequest request);
    void removeDibs(RemoveDibsServiceRequest request);
    Page<DibsResponse> getDibsListByDrawer(Long drawerId, Pageable pageable);

}
