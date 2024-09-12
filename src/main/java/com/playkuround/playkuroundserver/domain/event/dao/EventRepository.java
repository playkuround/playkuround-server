package com.playkuround.playkuroundserver.domain.event.dao;

import com.playkuround.playkuroundserver.domain.event.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByDisplay(boolean display);

}
