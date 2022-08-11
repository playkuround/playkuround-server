package com.playkuround.playkuroundserver.global.util;

import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.error.ErrorResponse;
import org.springframework.http.ResponseEntity;

public class ApiUtils {

    private ApiUtils() {}

    public static <T> ApiResponse<T> success(T response) {
        return ApiResponse.create(true, response, null);
    }

    public static ResponseEntity<?> error(ErrorResponse errorResponse) {
        return ResponseEntity
                .status(errorResponse.getStatus())
                .body(ApiResponse.create(false, null, errorResponse));
    }

}
