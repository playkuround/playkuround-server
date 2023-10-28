package com.playkuround.playkuroundserver.domain.landmark.exception;

import com.playkuround.playkuroundserver.global.error.exception.NotFoundException;

public class LandmarkNotFoundException extends NotFoundException {

    public LandmarkNotFoundException(Long landmarkId) {
        super(landmarkId + "의 랜드마크 조회에 실패하였습니다.");
    }

}
