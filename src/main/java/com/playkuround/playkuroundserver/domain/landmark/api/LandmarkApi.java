package com.playkuround.playkuroundserver.domain.landmark.api;

import com.playkuround.playkuroundserver.domain.landmark.application.LandmarkFindNearestService;
import com.playkuround.playkuroundserver.domain.landmark.dto.FindNearestLandmark;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/landmarks")
@RequiredArgsConstructor
public class LandmarkApi {

    private final LandmarkFindNearestService landmarkFindNearestService;

    @GetMapping("/nearest")
    public ApiResponse<FindNearestLandmark.Response> LandmarkFindNearest(@RequestBody @Valid FindNearestLandmark.Request findNearestLandmark) {
        FindNearestLandmark.Response nearestLandmarkResponse = landmarkFindNearestService.findNearestLandmark(findNearestLandmark);
        return ApiUtils.success(nearestLandmarkResponse);
    }
}
