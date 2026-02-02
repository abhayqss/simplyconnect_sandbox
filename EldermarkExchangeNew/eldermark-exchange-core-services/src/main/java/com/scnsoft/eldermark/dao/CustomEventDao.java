package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.client.report.EventCountByCommunityAndEventTypeItem;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;

public interface CustomEventDao {

    Map<Long, Long> countsByClientId(Specification<Event> spec);
    List<EventCountByCommunityAndEventTypeItem> countsByEventTypeAndCommunity(Specification<Event> spec);
    Map<Long, Pair<Long, Long>> countsAllAndWithRestrictionGroupByClientId(Specification<Event> spec, Specification<Event> restriction);
}
