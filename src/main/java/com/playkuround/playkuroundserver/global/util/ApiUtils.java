package com.playkuround.playkuroundserver.global.util;

import com.playkuround.playkuroundserver.global.common.response.ApiResult;
import com.playkuround.playkuroundserver.global.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiUtils {

    private ApiUtils() {}

    public static <T> ApiResult<T> success(T response) {
        new ApiResult<>(true, response, null);
        return new ApiResult<>(true, response, null);
    }

    public static ResponseEntity<ApiResult<ErrorResponse>> error(ErrorResponse errorResponse) {
        ApiResult<ErrorResponse> apiResult = new ApiResult<>(false, null, errorResponse);
        return new ResponseEntity<>(apiResult, HttpStatus.valueOf(apiResult.getErrorResponse().getStatus()));
    }

}
