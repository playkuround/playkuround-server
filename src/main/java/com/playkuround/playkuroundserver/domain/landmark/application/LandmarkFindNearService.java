package com.playkuround.playkuroundserver.domain.landmark.application;

import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.dto.response.NearestLandmarkResponse;
import com.playkuround.playkuroundserver.global.util.LocationDistanceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LandmarkFindNearService {

    private final LandmarkRepository landmarkRepository;

    public NearestLandmarkResponse findNearestLandmark(double latitude, double longitude) {
        List<Landmark> landmarks = landmarkRepository.findAll();

        NearestLandmarkResponse result = NearestLandmarkResponse.createEmptyResponse();
        for (Landmark landmark : landmarks) {
            double distance = LocationDistanceUtils.distance(landmark.getLatitude(), landmark.getLongitude(), latitude, longitude);
            if (distance <= landmark.getRecognitionRadius()) {
                result.update(landmark, distance);
            }
        }
        return result;
    }
}
