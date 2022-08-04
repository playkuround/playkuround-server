package com.playkuround.playkuroundserver.domain.landmark.application;

import com.playkuround.playkuroundserver.domain.landmark.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Adventure;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.dto.RequestSaveAdventure;
import com.playkuround.playkuroundserver.domain.landmark.dto.ResponseFindAdventure;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.domain.dao.UserRepository;
import com.playkuround.playkuroundserver.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdventureService {

    private final AdventureRepository adventureRepository;
    private final LandmarkRepository landmarkRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveAdventure(String userEmail, RequestSaveAdventure dto) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Landmark landmark = landmarkRepository.findById(dto.getLandmarkId())
                .orElseThrow(() -> new EntityNotFoundException("랜드마크를 찾을 수 없습니다."));

        validateLocation(dto.getLatitude(), dto.getLongitude(), landmark);

        adventureRepository.save(new Adventure(user, landmark));
    }

    private void validateLocation(Double latitude, Double longitude, Landmark landmark) {
        // TODO 랜드마크와 현재 위치에 대한 거리 검증 -> 검증 실패면 에러 발생
    }

    public List<ResponseFindAdventure> findAdventureByUserEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return adventureRepository.findAllByUser(user).stream()
                .map(adventure -> ResponseFindAdventure.builder()
                        .landmarkId(adventure.getLandmark().getId())
                        .createdDateTime(adventure.getCreateAt())
                        .build())
                .collect(Collectors.toList());

    }
}
