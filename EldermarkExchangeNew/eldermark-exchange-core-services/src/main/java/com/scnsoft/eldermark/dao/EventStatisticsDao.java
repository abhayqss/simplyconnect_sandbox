package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.EventGroupStatistics;
import com.scnsoft.eldermark.entity.event.Event;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface EventStatisticsDao {

    List<EventGroupStatistics> getByAllGroups(Specification<Event> specification);

}
