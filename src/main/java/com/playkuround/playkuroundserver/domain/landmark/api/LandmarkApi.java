package com.playkuround.playkuroundserver.domain.landmark.api;

import com.playkuround.playkuroundserver.domain.landmark.application.LandmarkFindNearService;
import com.playkuround.playkuroundserver.domain.landmark.dto.FindNearLandmark;
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

    private final LandmarkFindNearService landmarkFindNearService;

    @GetMapping
    public ApiResponse<FindNearLandmark.Response> LandmarkFindNear(@RequestBody @Valid FindNearLandmark.Request findNearLandmark) {
        FindNearLandmark.Response nearLandmarkResponse = landmarkFindNearService.findNearLandmark(findNearLandmark);
        return ApiUtils.success(nearLandmarkResponse);
    }
}
