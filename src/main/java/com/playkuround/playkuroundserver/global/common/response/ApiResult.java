package com.playkuround.playkuroundserver.global.common.response;

import com.playkuround.playkuroundserver.global.error.ErrorResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResult<T> {

    private final boolean success;
    private final T response;
    private final ErrorResponse errorResponse;

    private ApiResult(boolean success, T response, ErrorResponse errorResponse) {
        this.success = success;
        this.response = response;
        this.errorResponse = errorResponse;
    }

    public static <T> ApiResult<T> create(boolean success, T response, ErrorResponse errorResponse) {
        return new ApiResult<>(success, response, errorResponse);
    }

}
