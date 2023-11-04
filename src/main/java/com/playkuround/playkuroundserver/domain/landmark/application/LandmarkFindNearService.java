package com.playkuround.playkuroundserver.domain.landmark.application;

import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.dto.FindNearLandmarkResponse;
import com.playkuround.playkuroundserver.global.util.LocationDistanceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LandmarkFindNearService {

    private final LandmarkRepository landmarkRepository;

    @Transactional(readOnly = true)
    public FindNearLandmarkResponse findNearLandmark(double latitude, double longitude) {
        List<Landmark> landmarks = landmarkRepository.findAll();

        FindNearLandmarkResponse ret = FindNearLandmarkResponse.createEmptyResponse();
        for (Landmark landmark : landmarks) {
            double distance = LocationDistanceUtils.distance(landmark.getLatitude(), landmark.getLongitude(), latitude, longitude);
            if (distance <= landmark.getRecognitionRadius()) {
                ret.update(landmark, distance);
            }
        }
        return ret;
    }
}
