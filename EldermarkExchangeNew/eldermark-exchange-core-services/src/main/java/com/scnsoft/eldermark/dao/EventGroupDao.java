package com.scnsoft.eldermark.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scnsoft.eldermark.entity.event.EventGroup;

public interface EventGroupDao extends JpaRepository<EventGroup, Long> {

}
