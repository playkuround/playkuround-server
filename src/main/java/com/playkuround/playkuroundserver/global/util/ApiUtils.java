package com.playkuround.playkuroundserver.global.util;

import com.playkuround.playkuroundserver.global.common.response.ApiResult;
import com.playkuround.playkuroundserver.global.error.ErrorResponse;

public class ApiUtils {

    private ApiUtils() {}

    public static <T> ApiResult<T> success(T response) {
        return new ApiResult<>(true, response, null);
    }

    public static ApiResult<ErrorResponse> error(ErrorResponse errorResponse) {
        return new ApiResult<>(false, null, errorResponse);
    }

}
