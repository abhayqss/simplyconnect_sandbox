package com.scnsoft.eldermark.dump.dao;


import com.scnsoft.eldermark.dump.entity.Event;
import com.scnsoft.eldermark.dump.entity.Client;
import com.scnsoft.eldermark.dump.entity.EventTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EventDao extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    long countAllByClientAndEventTypeCodeIn(Client client, List<EventTypeEnum> eventTypeEnums);

}
