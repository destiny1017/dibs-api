package com.ably.dibs_api.controller;

import com.ably.dibs_api.config.common.ResponseDto;
import com.ably.dibs_api.controller.dto.AddDibsRequest;
import com.ably.dibs_api.controller.dto.AddDibsResponse;
import com.ably.dibs_api.domain.dibs.DibsService;
import com.ably.dibs_api.domain.dibs.dto.DibsResponse;
import com.ably.dibs_api.domain.dibs.dto.RemoveDibsServiceRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "찜 API", description = "찜 기능 API 리스트입니다.")
@RequestMapping("/v1/dibs")
public class DibsController {

    private final DibsService dibsService;

    @PostMapping("/add")
    public ResponseEntity<ResponseDto<?>> addDibs(@RequestBody @Valid AddDibsRequest request) {

        Long id = dibsService.addDibs(AddDibsRequest.toServiceRequest(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.ok(new AddDibsResponse(id)));
    }

    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<ResponseDto<?>> deleteDibs(@PathVariable("userId") Long userId,
                                                     @PathVariable("productId") Long productId) {
        RemoveDibsServiceRequest request = RemoveDibsServiceRequest.builder()
                .userId(userId)
                .productId(productId)
                .build();
        dibsService.removeDibs(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.ok());
    }

    @GetMapping("/{drawerId}")
    public ResponseEntity<ResponseDto<?>> getDibs(
            @PathVariable("drawerId") Long drawerId,
            @PageableDefault(sort = "id") Pageable pageable) {
        Page<DibsResponse> dibsList = dibsService.getDibsListByDrawer(drawerId, pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.ok(dibsList));
    }
}
