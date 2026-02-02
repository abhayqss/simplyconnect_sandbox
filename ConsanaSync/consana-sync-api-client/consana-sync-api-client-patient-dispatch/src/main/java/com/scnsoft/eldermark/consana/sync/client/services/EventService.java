package com.scnsoft.eldermark.consana.sync.client.services;

import com.scnsoft.eldermark.consana.sync.client.model.entities.Event;

import java.util.Optional;

public interface EventService {

    Optional<Event> findById(Long eventId);
}
