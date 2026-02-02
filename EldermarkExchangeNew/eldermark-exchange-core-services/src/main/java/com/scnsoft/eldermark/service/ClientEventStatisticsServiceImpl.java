package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.EventGroupStatistics;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.EventStatisticsDao;
import com.scnsoft.eldermark.dao.predicate.ClientPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.EventPredicateGenerator;
import com.scnsoft.eldermark.dao.specification.EventSpecificationGenerator;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.Event_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClientEventStatisticsServiceImpl implements ClientEventStatisticsService {

    @Autowired
    private EventStatisticsDao eventStatisticsDao;

    @Autowired
    private EventSpecificationGenerator eventSpecificationGenerator;

    @Autowired
    private EventPredicateGenerator eventPredicateGenerator;

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    @Override
    public List<EventGroupStatistics> findEventStatistics(Long clientId, PermissionFilter permissionFilter, Instant fromDate, Instant toDate) {
        //todo test when dashboard is implemented
        var forClientBetweenDates = specForClientBetweenDates(clientId, fromDate, toDate);
        var hasAccess = eventSpecificationGenerator.hasAccess(permissionFilter);
        return eventStatisticsDao.getByAllGroups(forClientBetweenDates.and(hasAccess));
    }

    private Specification<Event> specForClientBetweenDates(Long clientId, Instant fromDate, Instant toDate) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var clientJoin = root.join(Event_.client);

            var byClients = clientPredicateGenerator.clientAndMergedClients(criteriaBuilder, clientJoin, criteriaQuery, Collections.singletonList(clientId));
            var from = eventPredicateGenerator.from(fromDate, criteriaBuilder, root);
            var to = eventPredicateGenerator.to(toDate, criteriaBuilder, root);

            return criteriaBuilder.and(byClients, from, to);
        };
    }


}
