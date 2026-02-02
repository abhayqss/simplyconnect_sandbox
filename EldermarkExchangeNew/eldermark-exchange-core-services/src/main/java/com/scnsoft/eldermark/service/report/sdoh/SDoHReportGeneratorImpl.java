package com.scnsoft.eldermark.service.report.sdoh;

import com.scnsoft.eldermark.beans.reports.model.sdoh.*;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.dao.SdohReportRowDataDao;
import com.scnsoft.eldermark.dao.ServicePlanGoalDao;
import com.scnsoft.eldermark.dao.ServicePlanNeedDao;
import com.scnsoft.eldermark.dao.predicate.CommunityPredicateGenerator;
import com.scnsoft.eldermark.dao.specification.SdohReportRowDataSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.ServicePlanSpecificationGenerator;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.InNetworkInsurance_;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.sdoh.SdohReportLog;
import com.scnsoft.eldermark.entity.sdoh.SdohReportRowData;
import com.scnsoft.eldermark.entity.serviceplan.*;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.beans.reports.model.sdoh.SdoHRowType.*;

@Service
@Transactional(readOnly = true)
public class SDoHReportGeneratorImpl implements SDoHReportGenerator {

    private static final Sort GOALS_BY_LAST_MODIFIED_DESC = Sort.by(Sort.Direction.DESC,
            ServicePlanGoal_.NEED + "." + ServicePlanNeed_.SERVICE_PLAN + "." + ServicePlan_.LAST_MODIFIED_DATE
    );

    private static final List<String> CLIENT_INSURANCE_CODES = Arrays.asList(
            "AMERICAN_MEDICAL_SECURITY", "DEFINITY_HEALTH_UNITED_HEALTHCARE", "GOLDEN_RULE_UNITED_HEALTHCARE",
            "SECURE_HORIZONS_UNITED_HEALTHCARE", "UNITED_HEALTHCARE", "UNITEDHEALTHCARE",
            "UNITED_HEALTHCARE_COMMUNITY_PLAN", "UNITED_HEALTHCARE_OF_OHIO", "UNITEDHEALTHCARE_COMMUNITY_PLAN",
            "UNITEDHEALTHCARE_OF_OHIO");

    private static final String MEDICARE_PLAN = "Medicare";

    @Autowired
    private ServicePlanNeedDao servicePlanNeedDao;

    @Autowired
    private ServicePlanGoalDao servicePlanGoalDao;

    @Autowired
    private ServicePlanSpecificationGenerator servicePlanSpecificationGenerator;

    @Autowired
    private CommunityPredicateGenerator communityPredicateGenerator;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private SdohReportRowDataSpecificationGenerator rowDataSpecifications;

    @Autowired
    private SdohReportRowDataDao sdohReportRowDataDao;

    @Override
    public SDoHReport generateReport(SdohReportLog reportLog, PermissionFilter permissionFilter) {
        var reportFilter = new SDoHReportFilter();
        reportFilter.setInstantFrom(reportLog.getPeriodStart());
        reportFilter.setInstantTo(reportLog.getPeriodEnd());
        reportFilter.setOrganizationId(reportLog.getOrganization().getId());

        return generateReport(reportFilter, permissionFilter);
    }

    private SDoHReport generateReport(SDoHReportFilter filter, PermissionFilter permissionFilter) {
        var report = new SDoHReport();

        var needs = loadNeeds(filter, permissionFilter);

        var rows = needs.stream()
                .flatMap(need -> buildSDoHRows(need, filter.getInstantFrom(), filter.getInstantTo()))
                .sorted(Comparator.comparing(SDoHRow::getMemberLastName).thenComparing(SDoHRow::getMemberFirstName))
                .collect(Collectors.toList());

        report.setRows(rows);
        report.setSubmitterName(resolveSubmitter(rows, filter.getOrganizationId()));

        return report;
    }

    private List<ServicePlanNeed> loadNeeds(SDoHReportFilter filter, PermissionFilter permissionFilter) {
        var hasAccess = servicePlanSpecificationGenerator.hasAccessToNeed(permissionFilter);
        var unarchived = servicePlanSpecificationGenerator.unarchivedNeeds();

        //it's way easier to load all needs and goals and filter out 'within period' in java

        var sDoHNeedsOfOrganization = sDoHNeedsOfOrganization(filter.getOrganizationId());

        return servicePlanNeedDao.findAll(sDoHNeedsOfOrganization
                .and(hasAccess)
                .and(unarchived)
        );
    }

    private String resolveSubmitter(List<SDoHRow> rows, Long organizationId) {
        if (CollectionUtils.isNotEmpty(rows)) {
            return rows.get(0).getSubmitterName();
        } else {
            return organizationDao.getOne(organizationId).getSdohSubmitterName();
        }
    }

    private Specification<ServicePlanNeed> sDoHNeedsOfOrganization(Long organizationId) {
        return (servicePlanNeed, criteriaQuery, criteriaBuilder) -> {

            var client = servicePlanNeed.get(ServicePlanNeed_.servicePlan).get(ServicePlan_.client);
            var community = client.get(Client_.community);

            var discoverableCommunity = communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, community);
            var clientCommunitiesOfOrg = criteriaBuilder.equal(community.get(Community_.organizationId), organizationId);

            var activeClients = criteriaBuilder.equal(client.get(Client_.active), true);

            var clientInsurance = client.get(Client_.inNetworkInsurance).get(InNetworkInsurance_.key).in(CLIENT_INSURANCE_CODES);


            var withPresentProgramSubType = criteriaBuilder.isNotNull(servicePlanNeed.get(ServicePlanNeed_.programSubType));

            return criteriaBuilder.and(
                    clientCommunitiesOfOrg,
                    discoverableCommunity,
                    activeClients,
                    withPresentProgramSubType,
                    clientInsurance
            );

        };
    }

    private Stream<SDoHRow> buildSDoHRows(ServicePlanNeed servicePlanNeed, Instant periodStart, Instant periodEnd) {
        if (servicePlanNeed instanceof ServicePlanGoalNeed) {
            var goalNeed = (ServicePlanGoalNeed) servicePlanNeed;
            if (CollectionUtils.isNotEmpty(goalNeed.getGoals())) {
                return goalNeed.getGoals().stream()
                        .map(goal -> buildSDoHRow(goalNeed, goal, periodStart, periodEnd))
                        .filter(Optional::isPresent)
                        .map(Optional::get);
            }
        }
        return buildSDoHRow(servicePlanNeed, null, periodStart, periodEnd).stream();
    }

    private Optional<SDoHRow> buildSDoHRow(ServicePlanNeed servicePlanNeed, ServicePlanGoal goal, Instant periodStart, Instant periodEnd) {
        var type = resolveType(goal);

        var serviceDate = resolveServiceDate(type, servicePlanNeed, goal);
        if (!isServiceDateWithinPeriod(serviceDate, periodStart, periodEnd)) {
            return Optional.empty();
        }

        var servicePlan = servicePlanNeed.getServicePlan();
        var client = servicePlan.getClient();

        var rowDescriptor = SDohRowDescriptorFactory.create(type);
        applyConditionalRestrictions(rowDescriptor, client.getInsurancePlan());

        var row = new SDoHRow(rowDescriptor);

        row.setServicePlanId(servicePlan.getId());
        row.setNeedId(servicePlanNeed.getId());
        row.setGoalId(Optional.ofNullable(goal).map(ServicePlanGoal::getId).orElse(null));
        row.setClientInsurancePlan(client.getInsurancePlan());

        row.setSubmitterName(client.getOrganization().getSdohSubmitterName());
        row.setSourceSystem(client.getOrganization().getSdohSourceSystem());
        row.setMemberLastName(client.getLastName());
        row.setMemberFirstName(client.getFirstName());
        row.setMemberMiddleName(client.getMiddleName());
        row.setMemberDateOfBirth(client.getBirthDate());
        row.setMemberGender(mapGender(client.getGender()));

        CareCoordinationUtils.getFistNotNull(client.getPerson().getAddresses())
                .ifPresent(address -> {
                    row.setMemberAddress(address.getStreetAddress());
                    row.setMemberCity(address.getCity());
                    row.setMemberState(address.getState());
                    row.setMemberZipCode(address.getPostalCode());
                });

        row.setMemberHicn(cleanupHicn(client.getMedicareNumber()));

        row.setMemberCardId(client.getMemberNumber());
        row.setServiceDate(serviceDate);
        row.setIdentificationReferralFulfillment(type);
        row.setIcdOrMbrAttributionCode(resolveIcdOrMbrAttributionCode(servicePlanNeed));

        if (type != IDN) {
            if (goal != null) {
                row.setReferralFulfillmentProgramName(goal.getProviderName());
                row.setReferralFulfillmentProgramAddress(goal.getProviderAddress());
                row.setReferralFulfillmentProgramPhone(goal.getProviderPhone());
            }

            row.setRefFulProgramType(Optional.ofNullable(servicePlanNeed.getProgramType()).map(ProgramType::getDisplayName).orElse(null));
            row.setRefFulProgramSubtype(servicePlanNeed.getProgramSubType().getDisplayName());
        }

        return Optional.of(row);
    }

    private String cleanupHicn(String medicareNumber) {
        if (StringUtils.isEmpty(medicareNumber)) {
            return medicareNumber;
        }

        return medicareNumber.replaceAll("[- ]", "");
    }

    private SdoHRowType resolveType(ServicePlanGoal goal) {
        if (goal != null) {
            if (goal.getReferralServiceStatus() != null) {
                if (goal.getReferralServiceStatus() == ReferralServiceStatus.COMPLETED) {
                    return FUL;
                }
                return NFF;
            }
            if (StringUtils.isNotEmpty(goal.getProviderName())) {
                return REF;
            }
        }
        return IDN;
    }

    private Instant resolveServiceDate(SdoHRowType type, ServicePlanNeed need, ServicePlanGoal goal) {
        switch (type) {
            case IDN:
                return need.getServicePlan().getDateCreated();
            case REF:
                return goal.getCompletionDate();
            case FUL:
                return resolveDateWhenGoalServiceStatusBecameOneOf(goal, EnumSet.of(ReferralServiceStatus.COMPLETED));
            case NFF:
                return resolveDateWhenGoalServiceStatusBecameOneOf(goal, EnumSet.of(ReferralServiceStatus.PENDING,
                        ReferralServiceStatus.IN_PROCESS, ReferralServiceStatus.OTHER));
        }
        return null;
    }

    private Instant resolveDateWhenGoalServiceStatusBecameOneOf(ServicePlanGoal goal,
                                                                Set<ReferralServiceStatus> serviceStatuses) {
        var historyByChainId = servicePlanGoalDao.findAll(servicePlanSpecificationGenerator.goalHistoryByChainId(goal),
                GOALS_BY_LAST_MODIFIED_DESC);
        var historyGoalWithSameStatus = findEarliestGoalInSequenceWithOneOfServiceStatus(historyByChainId.stream(), serviceStatuses);

        if (historyGoalWithSameStatus.isEmpty()) {
            var fuzzyHistoryByGoalName = servicePlanGoalDao.findAll(servicePlanSpecificationGenerator.goalFuzzyHistoryByGoalName(goal));
            var clearedHistoryByGoalName = clearFuzzyGoalHistory(fuzzyHistoryByGoalName, goal);

            historyGoalWithSameStatus = findEarliestGoalInSequenceWithOneOfServiceStatus(
                    clearedHistoryByGoalName.sorted(Comparator.comparing(g -> g.getNeed().getServicePlan().getLastModifiedDate(), Comparator.reverseOrder())),
                    serviceStatuses);
        }

        return historyGoalWithSameStatus.or(() -> Optional.of(goal))
                .map(ServicePlanGoal::getNeed)
                .map(ServicePlanGoalNeed::getServicePlan)
                .map(ServicePlan::getLastModifiedDate)
                .orElse(null);
    }

    private Stream<ServicePlanGoal> clearFuzzyGoalHistory(List<ServicePlanGoal> fuzzyHistoryByGoalName, ServicePlanGoal originalGoal) {
        var groupedByServicePlan = fuzzyHistoryByGoalName.stream().collect(Collectors.groupingBy(
                goal -> goal.getNeed().getServicePlan().getId())
        );


        return groupedByServicePlan.values().stream()
                .map(goalList -> selectMostProbableHistoryGoal(goalList, originalGoal));
    }

    private ServicePlanGoal selectMostProbableHistoryGoal(List<ServicePlanGoal> goalList, ServicePlanGoal originalGoal) {
        //production service plans (except for service plans created for testing on production) have few service plans
        //containing multiple goals with the same goal names across history (therefore goalList will contain more than one entry),
        //but targetCompletionDate of such goals wasn't modified, so we will use this field to select most probable goal history
        //entry

        var withSameTargetCompletionDate = goalList.stream()
                .filter(goal -> originalGoal.getTargetCompletionDate().equals(goal.getTargetCompletionDate()))
                .findFirst();

        return withSameTargetCompletionDate.orElse(goalList.get(0));
    }

    private boolean isServiceDateWithinPeriod(Instant serviceDate, Instant periodStart, Instant periodEnd) {
        return serviceDate != null && !serviceDate.isBefore(periodStart) && !serviceDate.isAfter(periodEnd);
    }

    private Optional<ServicePlanGoal> findEarliestGoalInSequenceWithOneOfServiceStatus(Stream<ServicePlanGoal> history,
                                                                                       Set<ReferralServiceStatus> serviceStatuses) {
        //pre - history is sorted by servicePlan.lastModified desc
        return history
                .takeWhile(goal -> serviceStatuses.contains(goal.getReferralServiceStatus()))
                .reduce((first, second) -> second);
    }

    private String resolveIcdOrMbrAttributionCode(ServicePlanNeed need) {
        return Optional.ofNullable(need.getProgramSubType())
                .map(ProgramSubType::getzCode)
                .map(ZCode::getCode)
                .orElse(null);
    }

    private String mapGender(CcdCode gender) {
        if (gender == null) {
            return "U";
        }
        if ("M".equals(gender.getCode()) || "F".equals(gender.getCode())) {
            return gender.getCode();
        }
        return "U";
    }

    @Override
    public SDoHReport restoreSentReport(SdohReportLog reportLog, PermissionFilter permissionFilter) {
        var hasAccess = rowDataSpecifications.hasAccess(permissionFilter);
        var byReportLogId = rowDataSpecifications.byReportLogId(reportLog.getId());

        var rowData = sdohReportRowDataDao.findAll(hasAccess.and(byReportLogId));

        var report = new SDoHReport();

        var rows = rowData.stream()
                .map(this::restoreRow)
                .collect(Collectors.toList());

        report.setRows(rows);

        report.setSubmitterName(reportLog.getLastZipDownloadSubmitterName());
        return report;
    }

    private SDoHRow restoreRow(SdohReportRowData rowData) {
        var rowDescriptor = SDohRowDescriptorFactory.create(rowData.getIdentificationReferralFulfillment());
        applyConditionalRestrictions(rowDescriptor, rowData.getClientInsurancePlan());

        var row = new SDoHRow(rowDescriptor);

        row.setServicePlanId(rowData.getServicePlanId());
        row.setNeedId(rowData.getNeedId());
        row.setGoalId(rowData.getGoalId());
        row.setClientInsurancePlan(rowData.getClientInsurancePlan());

        row.setSubmitterName(rowData.getSubmitterName());
        row.setSourceSystem(rowData.getSourceSystem());
        row.setMemberLastName(rowData.getMemberLastName());
        row.setMemberFirstName(rowData.getMemberFirstName());
        row.setMemberMiddleName(rowData.getMemberMiddleName());
        row.setMemberDateOfBirth(rowData.getMemberDateOfBirth());
        row.setMemberGender(rowData.getMemberGender());

        row.setMemberAddress(rowData.getMemberAddress());
        row.setMemberCity(rowData.getMemberCity());
        row.setMemberState(rowData.getMemberState());
        row.setMemberZipCode(rowData.getMemberZipCode());

        row.setMemberHicn(rowData.getMemberHicn());

        row.setMemberCardId(rowData.getMemberCardId());
        row.setServiceDate(rowData.getServiceDate());
        row.setIdentificationReferralFulfillment(rowData.getIdentificationReferralFulfillment());
        row.setIcdOrMbrAttributionCode(rowData.getIcdOrMbrAttributionCode());

        row.setReferralFulfillmentProgramName(rowData.getReferralFulfillmentProgramName());
        row.setReferralFulfillmentProgramAddress(rowData.getReferralFulfillmentProgramAddress());
        row.setReferralFulfillmentProgramPhone(rowData.getReferralFulfillmentProgramPhone());
        row.setRefFulProgramType(rowData.getRefFulProgramType());
        row.setRefFulProgramSubtype(rowData.getRefFulProgramSubtype());

        return row;
    }

    @Override
    public List<SdohReportRowData> createRowData(SDoHReport report) {
        return report.getRows().stream().map(row -> {

            var descriptor = row.getRowDescriptor();
            var dataRow = new SdohReportRowData();

            dataRow.setServicePlanId(row.getServicePlanId());
            dataRow.setNeedId(row.getNeedId());
            dataRow.setGoalId(row.getGoalId());
            dataRow.setClientInsurancePlan(row.getClientInsurancePlan());
            dataRow.setSubmitterName(CareCoordinationUtils.truncate(row.getSubmitterName(), descriptor.getSubmitterName().getLength()));
            dataRow.setSourceSystem(CareCoordinationUtils.truncate(row.getSourceSystem(), descriptor.getSourceSystem().getLength()));
            dataRow.setMemberLastName(CareCoordinationUtils.truncate(row.getMemberLastName(), descriptor.getMemberLastName().getLength()));
            dataRow.setMemberFirstName(CareCoordinationUtils.truncate(row.getMemberFirstName(), descriptor.getMemberFirstName().getLength()));
            dataRow.setMemberMiddleName(CareCoordinationUtils.truncate(row.getMemberMiddleName(), descriptor.getMemberMiddleName().getLength()));
            dataRow.setMemberDateOfBirth(row.getMemberDateOfBirth());
            dataRow.setMemberGender(CareCoordinationUtils.truncate(row.getMemberGender(), descriptor.getMemberGender().getLength()));
            dataRow.setMemberAddress(CareCoordinationUtils.truncate(row.getMemberAddress(), descriptor.getMemberAddress().getLength()));
            dataRow.setMemberCity(CareCoordinationUtils.truncate(row.getMemberCity(), descriptor.getMemberCity().getLength()));
            dataRow.setMemberState(CareCoordinationUtils.truncate(row.getMemberState(), descriptor.getMemberState().getLength()));
            dataRow.setMemberZipCode(CareCoordinationUtils.truncate(row.getMemberZipCode(), descriptor.getMemberZipCode().getLength()));
            dataRow.setMemberHicn(CareCoordinationUtils.truncate(row.getMemberHicn(), descriptor.getMemberHicn().getLength()));
            dataRow.setMemberCardId(CareCoordinationUtils.truncate(row.getMemberCardId(), descriptor.getMemberCardId().getLength()));
            dataRow.setServiceDate(row.getServiceDate());
            dataRow.setIdentificationReferralFulfillment(row.getIdentificationReferralFulfillment());
            dataRow.setIcdOrMbrAttributionCode(CareCoordinationUtils.truncate(row.getIcdOrMbrAttributionCode(), descriptor.getIcdOrMbrAttributionCode().getLength()));
            dataRow.setReferralFulfillmentProgramName(CareCoordinationUtils.truncate(row.getReferralFulfillmentProgramName(), descriptor.getReferralFulfillmentProgramName().getLength()));
            dataRow.setReferralFulfillmentProgramAddress(CareCoordinationUtils.truncate(row.getReferralFulfillmentProgramAddress(), descriptor.getReferralFulfillmentProgramAddress().getLength()));
            dataRow.setReferralFulfillmentProgramPhone(CareCoordinationUtils.truncate(row.getReferralFulfillmentProgramPhone(), descriptor.getReferralFulfillmentProgramPhone().getLength()));
            dataRow.setRefFulProgramType(CareCoordinationUtils.truncate(row.getRefFulProgramType(), descriptor.getRefFulProgramType().getLength()));
            dataRow.setRefFulProgramSubtype(CareCoordinationUtils.truncate(row.getRefFulProgramSubtype(), descriptor.getRefFulProgramSubtype().getLength()));

            return dataRow;
        }).collect(Collectors.toList());
    }

    private void applyConditionalRestrictions(SDohRowDescriptor rowDescriptor, String clientInsurancePlan) {
        if (MEDICARE_PLAN.equalsIgnoreCase(clientInsurancePlan)) {
            rowDescriptor.getMemberHicn().setRequired(true);
        }
    }
}
