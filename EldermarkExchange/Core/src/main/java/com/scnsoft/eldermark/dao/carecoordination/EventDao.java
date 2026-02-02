package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDao;
import com.scnsoft.eldermark.entity.Event;
import com.scnsoft.eldermark.entity.EventListItemDbo;
import com.scnsoft.eldermark.shared.carecoordination.events.EventFilterDto;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author knetkachou
 * @author mradzivonenka
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 24-Sep-15.
 */
public interface EventDao extends BaseDao<Event> {
    List<EventListItemDbo> getEventsForEmployee(final EventFilterDto eventFilter, final Long databaseId, final Set<Long> communityIds, final Pageable pageRequest, boolean isAdmin, List<Long> viewableResidentAndMergedResidentIds, Map<Long, List<Long>> residentsWithNotViewableTypes, Set<Long> employeeCommunityIds);

    List<EventListItemDbo> getEvents(EventFilterDto eventFilter, List<Long> residentIds, Pageable pageable);

    Long getEventsCountForEmployee(final EventFilterDto eventFilter, final Long databaseId, final Set<Long> communityIds, boolean isAdmin, List<Long> viewableResidentAndMergedResidentIds, Map<Long, List<Long>> residentsWithNotViewableTypes, Set<Long> employeeCommunityIds);

    Long getEventsCountForEmployee(final EventFilterDto eventFilter, List<Long> residentIds);

    Integer getPageNumber(Long eventId,Long databaseId,  Set<Long> communityIds, boolean isAdmin, List<Long> accessibleResidentAndMergedResidentIds, Set<Long> employeeCommunityIds);

    Date getEventsMinimumDate(List<Long> residentIds);

    List<Long> getEventsIdsForEmployee(Long databaseId, Set<Long> communityIds, boolean isAdmin, List<Long> accessibleResidentAndMergedResidentIds, Set<Long> employeeCommunityIds);

    Map<Long,Long> getEventsCountGroupedByResidentId(List<Long> viewableResidentAndMergedResidentIds, Map<Long, List<Long>> residentsWithNotViewableTypes, Set<Long> communityIds, Set<Long> employeeCommunityIds);

}
