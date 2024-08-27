package com.playkuround.playkuroundserver.domain.event.api;

import com.playkuround.playkuroundserver.domain.event.api.request.EventSaveRequest;
import com.playkuround.playkuroundserver.domain.event.api.response.EventResponse;
import com.playkuround.playkuroundserver.domain.event.application.EventService;
import com.playkuround.playkuroundserver.domain.event.domain.Event;
import com.playkuround.playkuroundserver.domain.event.dto.EventSaveDto;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("events")
    public ApiResponse<List<EventResponse>> findDisplayEvents() {
        List<Event> events = eventService.findEvents(true);
        return ApiUtils.success(events.stream()
                .map(EventResponse::from)
                .toList());
    }

    @PostMapping("admin/events")
    public ApiResponse<Void> saveEvent(@RequestBody EventSaveRequest request) {
        EventSaveDto eventSaveDto = new EventSaveDto(request.getTitle(), request.getImageUrl(), request.getDescription(),
                request.getReferenceUrl(), request.isDisplay());
        eventService.saveEvent(eventSaveDto);

        return ApiUtils.success(null);
    }
}
