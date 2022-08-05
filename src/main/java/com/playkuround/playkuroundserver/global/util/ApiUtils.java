package com.playkuround.playkuroundserver.global.util;

import com.playkuround.playkuroundserver.global.common.response.ApiResult;
import com.playkuround.playkuroundserver.global.error.ErrorResponse;
import org.springframework.http.ResponseEntity;

public class ApiUtils {

    private ApiUtils() {}

    public static <T> ApiResult<T> success(T response) {
        return ApiResult.create(true, response, null);
    }

    public static ResponseEntity<?> error(ErrorResponse errorResponse) {
        return ResponseEntity.status(errorResponse.getStatus()).body(ApiResult.create(false, null, errorResponse));
    }

}
