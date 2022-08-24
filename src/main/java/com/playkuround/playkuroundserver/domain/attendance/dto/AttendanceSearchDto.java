package com.playkuround.playkuroundserver.domain.attendance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class AttendanceSearchDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private List<LocalDateTime> attendances;

        public static Response of(List<LocalDateTime> attendances) {
            return Response.builder()
                    .attendances(attendances)
                    .build();
        }
    }

}
