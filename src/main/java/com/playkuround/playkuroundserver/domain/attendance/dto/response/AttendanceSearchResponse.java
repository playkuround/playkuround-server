package com.playkuround.playkuroundserver.domain.attendance.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class AttendanceSearchResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private List<LocalDateTime> attendances;

    public static AttendanceSearchResponse from(List<LocalDateTime> attendances) {
        return new AttendanceSearchResponse(attendances);
    }
}
