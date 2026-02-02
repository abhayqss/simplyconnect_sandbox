package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.projection.*;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.optinout.OptInOutReport;
import com.scnsoft.eldermark.beans.reports.model.optinout.OptInOutReportClientRow;
import com.scnsoft.eldermark.beans.reports.model.optinout.OptInOutReportCommunityRow;
import com.scnsoft.eldermark.beans.reports.model.optinout.OptInOutReportRow;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dao.CommunityHieConsentPolicyDao;
import com.scnsoft.eldermark.dao.history.ClientHistoryDao;
import com.scnsoft.eldermark.dao.specification.ClientHistorySpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.ClientSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.CommunityHieConsentPolicySpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.CommunitySpecificationGenerator;
import com.scnsoft.eldermark.entity.client.report.HieConsentPolicyDetailsItem;
import com.scnsoft.eldermark.entity.hieconsentpolicy.CommunityHieConsentPolicy_;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class OptInOutReportGenerator extends DefaultReportGenerator<OptInOutReport> {

    private static final Sort COMMUNITY_HIE_CONSENT_POLICY_SORT_ORDER = Sort.by(Sort.Order.desc(CommunityHieConsentPolicy_.LAST_MODIFIED_DATE));

    @Autowired
    private ClientHistorySpecificationGenerator clientHistorySpecificationGenerator;
    @Autowired
    private CommunityHieConsentPolicySpecificationGenerator communityHieConsentPolicySpecificationGenerator;
    @Autowired
    private ClientSpecificationGenerator clientSpecificationGenerator;
    @Autowired
    private CommunitySpecificationGenerator communitySpecificationGenerator;
    @Autowired
    private ClientDao clientDao;
    @Autowired
    private ClientHistoryDao clientHistoryDao;
    @Autowired
    private CommunityDao communityDao;
    @Autowired
    private CommunityHieConsentPolicyDao communityHieConsentPolicyDao;
    @Autowired
    private Converter<HieConsentPolicyDetailsAware, HieConsentPolicyDetailsItem> hieConsentPolicyDetailsItemConverter;
    @Autowired
    private Converter<ClientHieConsentPolicyDetailsAware, HieConsentPolicyDetailsItem> clientHieConsentPolicyDetailsItemConverter;

    @Override
    public OptInOutReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var report = new OptInOutReport();
        populateReportingCriteriaFields(filter, report);
        report.setRows(createOptInOutRows(filter, permissionFilter));
        return report;
    }

    private List<OptInOutReportRow> createOptInOutRows(InternalReportFilter filter, PermissionFilter permissionFilter) {

        var clientHistory = Stream.concat(
                        getClientHieConsentPolicyDetails(filter, permissionFilter).stream(),
                        getClientHistoryHieConsentPolicyDetails(filter, permissionFilter).stream()
                )
                .sorted(
                        Comparator.comparing(HieConsentPolicyDetailsItem::getCommunityName)
                                .thenComparing(HieConsentPolicyDetailsItem::getCommunityId)
                                .thenComparing(HieConsentPolicyDetailsItem::getClientFullName)
                                .thenComparing(Comparator.comparing(HieConsentPolicyDetailsItem::getClientUpdateDatetime).reversed())
                )
                .collect(Collectors.toList());

        var communityHistory = getCommunityHieConsentPolicy(filter);

        var stateHiePolicyMap = getCommunityStateHieConsentPolicy(filter);

        var communities = filter.getAccessibleCommunityIdsAndNames().stream()
                .sorted(Comparator.comparing(NameAware::getName))
                .collect(Collectors.toList());

        return List.of(createOrganizationRow(clientHistory, communityHistory, communities, stateHiePolicyMap));
    }

    private Map<Long, HieConsentPolicyType> getCommunityStateHieConsentPolicy(InternalReportFilter filter) {
        var communitySpecification = communitySpecificationGenerator.byCommunityIdsEligibleForDiscovery(
                filter.getAccessibleCommunityIdsAndNames().stream()
                        .map(IdAware::getId)
                        .collect(Collectors.toList())
        );
        return communityDao.findCommunityStatePolicy(communitySpecification);
    }

    private List<CommunityHieConsentPolicyDetailsAware> getCommunityHieConsentPolicy(InternalReportFilter filter) {
        var byCommunityIdIn =
                communityHieConsentPolicySpecificationGenerator.byCommunityIdIn(filter.getAccessibleCommunityIdsAndNames());
        var byPeriod = communityHieConsentPolicySpecificationGenerator.byLastModifiedInPeriod(filter.getInstantFrom(), filter.getInstantTo());
        var latestBeforeDate = communityHieConsentPolicySpecificationGenerator.latestBeforeDate(filter.getInstantFrom());
        var specification = byCommunityIdIn.and(byPeriod.or(latestBeforeDate));

        return communityHieConsentPolicyDao.findAll(specification, CommunityHieConsentPolicyDetailsAware.class, COMMUNITY_HIE_CONSENT_POLICY_SORT_ORDER);
    }

    private List<HieConsentPolicyDetailsItem> getClientHistoryHieConsentPolicyDetails(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var clientHistorySpecification =
                clientHistorySpecificationGenerator.hasAccess(permissionFilter)
                        .and(clientHistorySpecificationGenerator.withinReportPeriod(filter.getInstantFrom(), filter.getInstantTo()))
                        .and(clientHistorySpecificationGenerator.isHieConsentPolicyTypeNotNull());
        var optInOutHistoryDetails =
                clientHistoryDao.findAll(clientHistorySpecification, ClientHieConsentPolicyDetailsAware.class);
        return optInOutHistoryDetails.stream()
                .map(clientHieConsentPolicyDetailsItemConverter::convert)
                .collect(Collectors.toList());
    }

    private List<HieConsentPolicyDetailsItem> getClientHieConsentPolicyDetails(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var clientSpecification = clientSpecificationGenerator.hasDetailsAccess(permissionFilter)
                .and(clientSpecificationGenerator.byCommunities(filter.getAccessibleCommunityIdsAndNames()))
                .and(clientSpecificationGenerator.lastUpdatedBeforeOrEqual(filter.getInstantTo()));
        var optInOutDetails = clientDao.findAll(clientSpecification, HieConsentPolicyDetailsAware.class);
        return optInOutDetails.stream()
                .map(hieConsentPolicyDetailsItemConverter::convert)
                .collect(Collectors.toList());
    }

    private OptInOutReportRow createOrganizationRow(
            List<HieConsentPolicyDetailsItem> clientHistory,
            List<CommunityHieConsentPolicyDetailsAware> communitiesHistory,
            List<IdNameAware> communities,
            Map<Long, HieConsentPolicyType> communityStatePolicyMap
    ) {
        var row = new OptInOutReportRow();

        var communityRows = communities.stream()
                .flatMap(community -> {
                    var communityId = community.getId();
                    var communityClientHistory = clientHistory.stream()
                            .filter(it -> Objects.equals(it.getCommunityId(), communityId))
                            .collect(Collectors.toList());

                    if (communityClientHistory.isEmpty()) return Stream.empty();

                    var communityHiePolicyHistory = communitiesHistory.stream()
                            .filter(it -> Objects.equals(it.getCommunityId(), communityId))
                            .collect(Collectors.toList());

                    var communityStatePolicy = Optional.ofNullable(communityStatePolicyMap.get(communityId))
                            .orElse(HieConsentPolicyType.OPT_OUT);

                    var communityRow = createCommunityRow(community, communityClientHistory, communityHiePolicyHistory, communityStatePolicy);

                    return Stream.of(communityRow);
                })
                .collect(Collectors.toList());

        row.setCommunityRows(communityRows);

        return row;
    }

    private OptInOutReportCommunityRow createCommunityRow(
            IdNameAware community,
            List<HieConsentPolicyDetailsItem> clientHistory,
            List<CommunityHieConsentPolicyDetailsAware> communityPolicyHistory,
            HieConsentPolicyType stateHiePolicy
    ) {
        var communityRow = new OptInOutReportCommunityRow();

        communityRow.setCommunityName(community.getName());
        communityRow.setDefaultCommunityPolicies(createCommunityPolicyItems(communityPolicyHistory, stateHiePolicy));

        var clientRows = clientHistory.stream()
                .map(HieConsentPolicyDetailsItem::getClientId)
                .distinct()
                .map(clientId -> clientHistory.stream()
                        .filter(it -> Objects.equals(it.getClientId(), clientId))
                        .collect(Collectors.toList()))
                .map(this::createClientRow)
                .collect(Collectors.toList());

        communityRow.setClientRows(clientRows);
        return communityRow;
    }

    private List<OptInOutReportCommunityRow.DefaultCommunityHieConsentPolicy> createCommunityPolicyItems(
            List<CommunityHieConsentPolicyDetailsAware> communityHistory,
            HieConsentPolicyType stateHiePolicy
    ) {
        if (CollectionUtils.isEmpty(communityHistory)) {
            return List.of(new OptInOutReportCommunityRow.DefaultCommunityHieConsentPolicy(stateHiePolicy));
        }

        return communityHistory.stream()
                .map(policy -> new OptInOutReportCommunityRow.DefaultCommunityHieConsentPolicy(policy.getType(), policy.getLastModifiedDate()))
                .collect(Collectors.toList());
    }

    private OptInOutReportClientRow createClientRow(List<HieConsentPolicyDetailsItem> clients) {
        var clientRow = new OptInOutReportClientRow();
        var client = clients.get(0);

        clientRow.setClientId(client.getClientId());
        clientRow.setClientStatus(resolveClientStatus(clients.stream()
                .max(Comparator.comparing(HieConsentPolicyDetailsItem::getIsClientActive))
                .map(HieConsentPolicyDetailsItem::getIsClientActive)
                .orElse(null)));
        clientRow.setFullClientName(client.getClientFullName());

        clients.forEach(details -> addToClientRow(clientRow, details));

        return clientRow;
    }

    private void addToClientRow(OptInOutReportClientRow clientRow, HieConsentPolicyDetailsItem details) {
        var previousPolicy = !CollectionUtils.isEmpty(clientRow.getPolicies())
                ? clientRow.getPolicies().getLast()
                : null;
        if (previousPolicy == null || isUniquePolicyData(previousPolicy, details)) {
            var dto = new OptInOutReportClientRow.HieConsentPolicy();
            var policyType = details.getHieConsentPolicyType();
            dto.setStatusUpdateTime(details.getClientUpdateDatetime());
            dto.setStatus(policyType);
            dto.setObtainedDate(details.getHieConsentPolicyUpdateDateTime());
            dto.setSource(details.getHieConsentPolicySource());
            dto.setObtainedBy(details.getHieConsentPolicyObtainedBy());
            dto.setObtainedFrom(details.getOptInOutObtainedFrom());
            clientRow.getPolicies().add(dto);
        }
    }

    private boolean isUniquePolicyData(OptInOutReportClientRow.HieConsentPolicy policy, HieConsentPolicyDetailsItem details) {
        return !Objects.equals(policy.getStatus(), details.getHieConsentPolicyType())
                || !Objects.equals(policy.getObtainedDate(), details.getHieConsentPolicyUpdateDateTime())
                || !Objects.equals(policy.getSource(), details.getHieConsentPolicySource())
                || !Objects.equals(policy.getObtainedBy(), details.getHieConsentPolicyObtainedBy());
    }

    @Override
    public ReportType getReportType() {
        return ReportType.OPT_IN_OUT;
    }
}
