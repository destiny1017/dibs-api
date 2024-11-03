package com.ably.dibs_api.config.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto<T> {

    private String code;
    private String message;
    private LocalDateTime timestamp;
    private T data;

    @Builder
    public ResponseDto(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.data = data;
    }

    public static <T> ResponseDto<T> of(String code, String message, T data) {
        return new ResponseDto<>(code, message, data);
    }

    public static <T> ResponseDto<T> ok(T data) {
        return of("SUCCESS", "성공", data);
    }

    public static <T> ResponseDto<T> ok() {
        return of("SUCCESS", "성공", null);
    }
}
