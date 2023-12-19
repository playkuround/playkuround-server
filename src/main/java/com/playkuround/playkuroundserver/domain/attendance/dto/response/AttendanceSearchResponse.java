package com.playkuround.playkuroundserver.domain.attendance.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class AttendanceSearchResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Schema(description = "출석 날짜(yyyy-MM-dd), 최신날짜가 배열의 뒤쪽에 위치합니다.", example = "[\"2023-12-01\", \"2023-12-03\", \"2023-12-09\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<LocalDateTime> attendances;

    public static AttendanceSearchResponse from(List<LocalDateTime> attendances) {
        return new AttendanceSearchResponse(attendances);
    }
}
