package com.playkuround.playkuroundserver.domain.attendance.exception;

import com.playkuround.playkuroundserver.global.error.exception.NotFoundException;

public class AttendanceNotFoundException extends NotFoundException {

    public AttendanceNotFoundException(String userEmail) {
        super(userEmail + " 의 출석 조회에 실패하였습니다.");
    }

}
