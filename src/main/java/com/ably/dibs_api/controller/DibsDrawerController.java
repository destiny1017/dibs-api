package com.ably.dibs_api.controller;

import com.ably.dibs_api.config.common.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@Slf4j
@RequestMapping("/v1/dibs-drawer")
public class DibsDrawerController {

    @GetMapping("/{drawerId}")
    public ResponseEntity<ResponseDto<?>> getDrawers(@PathVariable("drawerId") Long drawerId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.builder()
                        .code("SUCCESS")
                        .data(new ArrayList<>())
                        .build());
    }
}
