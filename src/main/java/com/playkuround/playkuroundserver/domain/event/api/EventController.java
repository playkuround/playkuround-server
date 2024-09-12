package com.playkuround.playkuroundserver.domain.event.api;

import com.playkuround.playkuroundserver.domain.event.api.request.EventSaveRequest;
import com.playkuround.playkuroundserver.domain.event.api.response.EventResponse;
import com.playkuround.playkuroundserver.domain.event.application.EventService;
import com.playkuround.playkuroundserver.domain.event.domain.Event;
import com.playkuround.playkuroundserver.domain.event.dto.EventSaveDto;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("events")
    @Operation(summary = "이벤트 조회하기", description = "화면에 노출이 필요한 이벤트를 반환합니다.", tags = "Event")
    public ApiResponse<List<EventResponse>> findDisplayEvents() {
        List<Event> events = eventService.findEvents(true);
        return ApiUtils.success(events.stream()
                .map(EventResponse::from)
                .toList());
    }

    @PostMapping("admin/events")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "이벤트 저장하기", description = "이벤트를 저장합니다.", tags = "Admin")
    public ApiResponse<Void> saveEvent(@RequestBody EventSaveRequest request) {
        EventSaveDto eventSaveDto = new EventSaveDto(request.getTitle(), request.getImageUrl(), request.getDescription(),
                request.getReferenceUrl(), request.isDisplay());
        eventService.saveEvent(eventSaveDto);

        return ApiUtils.success(null);
    }
}
