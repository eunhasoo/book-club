package com.eunhasoo.bookclub.common;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResponse {
    private String statusCode;
    private String message;

    private ErrorResponse() {
    }

    @Builder
    public ErrorResponse(String statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
