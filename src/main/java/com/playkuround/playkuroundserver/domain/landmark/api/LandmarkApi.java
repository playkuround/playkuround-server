package com.playkuround.playkuroundserver.domain.landmark.api;

import com.playkuround.playkuroundserver.domain.landmark.application.LandmarkFindNearService;
import com.playkuround.playkuroundserver.domain.landmark.dto.FindNearLandmark;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import com.playkuround.playkuroundserver.global.validation.Latitude;
import com.playkuround.playkuroundserver.global.validation.Longitude;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/landmarks")
@RequiredArgsConstructor
public class LandmarkApi {

    private final LandmarkFindNearService landmarkFindNearService;

    @GetMapping
    public ApiResponse<FindNearLandmark.Response> LandmarkFindNear(@RequestParam @Latitude Double latitude,
                                                                   @RequestParam @Longitude Double longitude) {
        FindNearLandmark.Response nearLandmarkResponse = landmarkFindNearService.findNearLandmark(latitude, longitude);
        return ApiUtils.success(nearLandmarkResponse);
    }
}
