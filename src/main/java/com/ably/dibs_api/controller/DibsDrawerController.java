package com.ably.dibs_api.controller;

import com.ably.dibs_api.config.common.ResponseDto;
import com.ably.dibs_api.controller.dto.CreateDibsDrawerRequest;
import com.ably.dibs_api.domain.dibs.DibsService;
import com.ably.dibs_api.domain.dibs.dto.CreateDibsDrawerServiceResponse;
import com.ably.dibs_api.domain.dibs.dto.DibsDrawerResponse;
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

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "찜 서랍 API", description = "찜 서랍 기능 API 리스트입니다.")
@RequestMapping("/v1/dibs-drawer")
public class DibsDrawerController {

    private final DibsService dibsService;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto<?>> createDibsDrawer(@RequestBody @Valid CreateDibsDrawerRequest request) {
        CreateDibsDrawerServiceResponse dibsDrawer =
                dibsService.createDibsDrawer(CreateDibsDrawerRequest.toServiceRequest(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.ok(dibsDrawer));
    }

    @DeleteMapping("/{drawerId}")
    public ResponseEntity<ResponseDto<?>> deleteDrawer(@PathVariable("drawerId") Long drawerId) {
        dibsService.deleteDibsDrawer(drawerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.ok());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDto<?>> getDrawers(
            @PathVariable("userId") Long userId,
            @PageableDefault(sort = "id") Pageable pageable) {
        Page<DibsDrawerResponse> myDrawerList = dibsService.getMyDrawerList(userId, pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.ok(myDrawerList));
    }
}
