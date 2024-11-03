package com.ably.dibs_api.config.error;

import com.ably.dibs_api.config.common.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseDto<Object>> businessExceptionHandler(BusinessException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(ResponseDto.builder()
                        .code("FAIL")
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Object>> bindExceptionHandler(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult()
                .getFieldError();
        String errorMessage = String.format("%s : %s", fieldError.getField(), fieldError.getDefaultMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.builder()
                        .code("FAIL")
                        .message(errorMessage)
                        .build());
    }
}
