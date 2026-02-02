package com.scnsoft.eldermark.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.scnsoft.eldermark.entity.event.Event;

public interface EventGroupService {

    Page<Event> find(Pageable pageable);
}
