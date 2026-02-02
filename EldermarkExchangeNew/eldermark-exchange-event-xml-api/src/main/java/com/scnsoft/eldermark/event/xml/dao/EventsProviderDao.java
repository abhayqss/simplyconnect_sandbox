package com.scnsoft.eldermark.event.xml.dao;

import com.scnsoft.eldermark.event.xml.entity.EventsProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventsProviderDao extends JpaRepository<EventsProvider, Long> {

    List<EventsProvider> getByLogin(final String login);
}
