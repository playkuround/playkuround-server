package com.playkuround.playkuroundserver.domain.landmark.application;

import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.dto.FindNearestLandmark;
import com.playkuround.playkuroundserver.global.util.LocationDistanceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LandmarkFindNearestService {

    private final LandmarkRepository landmarkRepository;

    public FindNearestLandmark.Response findNearestLandmark(FindNearestLandmark.Request requestForm) {
        List<Landmark> landmarks = landmarkRepository.findAll();

        double latitude = requestForm.getLatitude();
        double longitude = requestForm.getLongitude();

        Landmark nearLandmark = landmarks.get(0);
        double nearDistance = LocationDistanceUtils.distance(nearLandmark.getLatitude(), nearLandmark.getLongitude(), latitude, longitude);

        for (Landmark landmark : landmarks) {
            double distance = LocationDistanceUtils.distance(landmark.getLatitude(), landmark.getLongitude(), latitude, longitude);
            if (distance < nearDistance) {
                nearLandmark = landmark;
                nearDistance = distance;
            }
        }

        return FindNearestLandmark.Response.of(nearLandmark, nearDistance);
    }
}
