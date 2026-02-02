package com.scnsoft.eldermark.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.scnsoft.eldermark.dao.EventDao;
import com.scnsoft.eldermark.entity.event.Event;

@Service
public class EventGroupServiceImpl implements EventGroupService {

    @Autowired
    private EventDao eventDao;

    @Override
    public Page<Event> find(Pageable pageable) {
        return eventDao.findAll(pageable);
    }

}
