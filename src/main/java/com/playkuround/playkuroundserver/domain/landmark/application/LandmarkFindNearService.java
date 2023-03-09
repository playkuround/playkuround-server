package com.playkuround.playkuroundserver.domain.landmark.application;

import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.dto.FindNearLandmark;
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
    public FindNearLandmark.Response findNearLandmark(double latitude, double longitude) {
        List<Landmark> landmarks = landmarkRepository.findAll();

        FindNearLandmark.Response ret = new FindNearLandmark.Response();
        double nearDistance = -1;
        for (Landmark landmark : landmarks) {
            double distance = LocationDistanceUtils.distance(landmark.getLatitude(), landmark.getLongitude(), latitude, longitude);
            if (distance <= landmark.getRecognitionRadius()) {
                if (nearDistance == -1 || nearDistance > distance) {
                    ret = FindNearLandmark.Response.of(landmark, distance);
                    nearDistance = distance;
                }
            }
        }
        return ret;
    }
}
