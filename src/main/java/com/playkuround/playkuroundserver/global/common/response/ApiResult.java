package com.playkuround.playkuroundserver.global.common.response;

import com.playkuround.playkuroundserver.global.error.ErrorResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResult<T> {

    private final boolean isSuccess;
    private final T response;
    private final ErrorResponse errorResponse;

    public ApiResult(boolean isSuccess, T response, ErrorResponse errorResponse) {
        this.isSuccess = isSuccess;
        this.response = response;
        this.errorResponse = errorResponse;
    }

}
