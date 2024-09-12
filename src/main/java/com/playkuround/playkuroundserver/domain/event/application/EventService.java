package com.playkuround.playkuroundserver.domain.event.application;

import com.playkuround.playkuroundserver.domain.event.dao.EventRepository;
import com.playkuround.playkuroundserver.domain.event.domain.Event;
import com.playkuround.playkuroundserver.domain.event.dto.EventSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<Event> findEvents(boolean display) {
        return eventRepository.findByDisplay(display);
    }

    @Transactional
    public void saveEvent(EventSaveDto eventSaveDto) {
        Event event = new Event(eventSaveDto.title(), eventSaveDto.imageUrl(), eventSaveDto.description(),
                eventSaveDto.referenceUrl(), eventSaveDto.display());
        eventRepository.save(event);
    }
}
