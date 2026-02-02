package com.scnsoft.eldermark.mobile.facade.home;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.careteam.LatestCareTeamMemberModifiedDao;
import com.scnsoft.eldermark.dao.predicate.ClientPredicateGenerator;
import com.scnsoft.eldermark.entity.careteam.CareTeamMemberModificationType;
import com.scnsoft.eldermark.entity.careteam.LatestCareTeamMemberModified;
import com.scnsoft.eldermark.entity.careteam.LatestCareTeamMemberModified_;
import com.scnsoft.eldermark.mobile.dto.home.CareTeamUpdateHomeSectionDto;
import com.scnsoft.eldermark.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CareTeamUpdateHomeSectionProviderImpl implements CareTeamUpdateHomeSectionProvider {

    public static final Sort SORT_ORDER = Sort.by(Sort.Order.desc(LatestCareTeamMemberModified_.DATE_TIME));

    @Autowired
    private LatestCareTeamMemberModifiedDao latestCareTeamMemberModifiedDao;

    @Autowired
    private ClientService clientService;

    @Override
    public List<CareTeamUpdateHomeSectionDto> loadCareTeamUpdates(Long currentEmployeeId, Set<Long> clientIds, int limit) {
        var entities = latestCareTeamMemberModifiedDao.findAll(
                forReadingEmployeeId(currentEmployeeId).and(forClientsAndMerged(clientIds)),
                LastCareTeamModifiedHomeSection.class,
                SORT_ORDER,
                limit
        );

        //users can't access care team members list through merged clients, so we'll
        //substitute such clients with clients passed in clientIds parameter
        var clientIdsOfUpdates = entities.stream()
                .map(ClientIdAware::getClientId)
                .collect(Collectors.toSet());

        var mergedClientIds = clientIdsOfUpdates.stream()
                .filter(id -> !clientIds.contains(id))
                .collect(Collectors.toSet());


        Map<Long, Set<Long>> mergedMap;
        if (mergedClientIds.isEmpty()) {
            mergedMap = Map.of();
        } else {
            mergedMap = clientService.findMergedClientIdsAmong(mergedClientIds, clientIds);
        }

        return entities.stream()
                .map(entity -> convert(entity,
                        Optional.ofNullable(mergedMap.getOrDefault(entity.getClientId(), null))
                                .map(set -> set.iterator().next())
                                .orElse(entity.getClientId())))
                .collect(Collectors.toList());
    }

    private CareTeamUpdateHomeSectionDto convert(LastCareTeamModifiedHomeSection entity, Long clientId) {
        var dto = new CareTeamUpdateHomeSectionDto();
        dto.setUpdateId(entity.getId());
        dto.setAvatarId(entity.getCtmEmployeeAvatarId());
        dto.setAvatarName(entity.getCtmEmployeeAvatarAvatarName());
        dto.setCareTeamMemberId(entity.getCareTeamMemberId());
        dto.setFirstName(entity.getCtmEmployeeFirstName());
        dto.setLastName(entity.getCtmEmployeeLastName());
        dto.setStatus(entity.getModificationType());
        dto.setPerformerFirstName(entity.getPerformedByFirstName());
        dto.setPerformerLastName(entity.getPerformedByLastName());
        dto.setClientId(clientId);
        return dto;
    }

    private Specification<LatestCareTeamMemberModified> forReadingEmployeeId(Long employeeId) {
        //won't check AccessRight.Code.MY_CT_VISIBILITY because it is coming from old PHR mobile app and
        //not used anymore. Also according to current business logic user views updates of
        //his own care team records.
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(LatestCareTeamMemberModified_.readByEmployeeId), employeeId);
    }

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    private Specification<LatestCareTeamMemberModified> forClientsAndMerged(Collection<Long> clientIds) {
        return (root, query, criteriaBuilder) -> clientPredicateGenerator.clientAndMergedClients(
                criteriaBuilder,
                JpaUtils.getOrCreateJoin(root, LatestCareTeamMemberModified_.client),
                query,
                clientIds
        );
    }

    interface LastCareTeamModifiedHomeSection extends IdAware, ClientIdAware {
        Long getCareTeamMemberId();

        String getCtmEmployeeFirstName();

        String getCtmEmployeeLastName();

        Long getCtmEmployeeAvatarId();

        String getCtmEmployeeAvatarAvatarName();

        CareTeamMemberModificationType getModificationType();

        String getPerformedByFirstName();

        String getPerformedByLastName();
    }
}
