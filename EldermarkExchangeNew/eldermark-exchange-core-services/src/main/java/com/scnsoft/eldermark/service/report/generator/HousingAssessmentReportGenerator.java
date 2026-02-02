package com.scnsoft.eldermark.service.report.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.beans.projection.*;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.assessment.housing.HousingAssessment;
import com.scnsoft.eldermark.beans.reports.model.assessment.housing.HousingAssessmentReport;
import com.scnsoft.eldermark.beans.reports.model.assessment.housing.HousingAssessmentReportClientItem;
import com.scnsoft.eldermark.beans.reports.model.assessment.housing.HousingAssessmentReportItem;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ClientAssessmentDao;
import com.scnsoft.eldermark.dao.history.ClientHistoryDao;
import com.scnsoft.eldermark.dao.specification.ClientAssessmentResultSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.ClientHistorySpecificationGenerator;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult_;
import com.scnsoft.eldermark.entity.client.history.ClientHistoryStatusAware;
import com.scnsoft.eldermark.entity.community.Community_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.stream.Collectors;

@Service
public class HousingAssessmentReportGenerator extends DefaultReportGenerator<HousingAssessmentReport> {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientAssessmentDao clientAssessmentDao;

    @Autowired
    private ClientAssessmentResultSpecificationGenerator clientAssessmentResultSpecificationGenerator;

    @Autowired
    private ClientHistoryDao clientHistoryDao;

    @Autowired
    private ClientHistorySpecificationGenerator clientHistorySpecificationGenerator;

    @Override
    @Transactional(readOnly = true)
    public HousingAssessmentReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {

        var report = new HousingAssessmentReport();

        populateReportingCriteriaFields(filter, report);

        var assessments = clientAssessmentDao.findAll(
                clientAssessmentResultSpecificationGenerator.byType(Assessment.HOUSING)
                        .and(clientAssessmentResultSpecificationGenerator.hasAccess(permissionFilter))
                        .and(clientAssessmentResultSpecificationGenerator.completedWithinPeriod(filter.getInstantFrom(), filter.getInstantTo()))
                        .and(clientAssessmentResultSpecificationGenerator.latestCompletedBeforeDate(filter.getInstantTo()))
                        .and(clientAssessmentResultSpecificationGenerator.isUnarchived())
                        .and(clientAssessmentResultSpecificationGenerator.byClientInCommunities(filter.getAccessibleCommunityIdsAndNames())),
                HousingAssessmentReportDetailsAware.class,
                Sort.by(
                        String.join(".", ClientAssessmentResult_.CLIENT, Client_.COMMUNITY, Community_.NAME),
                        String.join(".", ClientAssessmentResult_.CLIENT, Client_.FIRST_NAME),
                        String.join(".", ClientAssessmentResult_.CLIENT, Client_.LAST_NAME)
                )
        );

        var clientStatuses = clientHistoryDao.findAll(
                        clientHistorySpecificationGenerator.latestForDate(filter.getInstantTo())
                                .and(clientHistorySpecificationGenerator.hasAccess(permissionFilter)),
                        ClientHistoryStatusAware.class
                )
                .stream()
                .collect(Collectors.toMap(ClientHistoryStatusAware::getClientId, ClientHistoryStatusAware::getActive));


        var items = new LinkedList<HousingAssessmentReportItem>();

        assessments.forEach(assessment -> {

            var communityName = assessment.getClientCommunityName();

            var item = items.isEmpty() ? null : items.getLast();
            if (item == null || !item.getClientCommunityName().equals(communityName)) {
                item = new HousingAssessmentReportItem();
                item.setClientCommunityName(communityName);
                item.setClients(new LinkedList<>());
                items.add(item);
            }

            var clientItem = new HousingAssessmentReportClientItem();

            clientItem.setClientId(assessment.getClientId());
            clientItem.setClientActive(clientStatuses.get(assessment.getClientId()));
            clientItem.setClientName(assessment.getClientFullName());
            clientItem.setAssessmentDate(assessment.getDateCompleted());

            fillAssessmentResults(assessment.getResult(), clientItem);

            item.getClients().add(clientItem);
        });


        report.setItems(items);

        return report;
    }

    private void fillAssessmentResults(String rawAssessment, HousingAssessmentReportClientItem clientItem) {
        var assessment = parseHousingAssessment(rawAssessment);
        clientItem.setProgram(assessment.getProgram());
        clientItem.setAssessmentType(assessment.getAssessmentType());
        clientItem.setLeaseQuestion(assessment.getLeaseQuestion());
        clientItem.setSubsidizedHousingQuestion(assessment.getSubsidizedHousingQuestion());
        clientItem.setHousingVoucherQuestion(assessment.getHousingVoucherQuestion());
        clientItem.setEvictionsQuestion(assessment.getEvictionsQuestion());
        clientItem.setAccessibilityNeedsQuestion(assessment.getAccessibilityNeedsQuestion());
        clientItem.setPetsQuestion(assessment.getPetsQuestion());
        clientItem.setIncomeQuestion(assessment.getIncomeQuestion());
        clientItem.setSavingsQuestion(assessment.getSavingsQuestion());
        clientItem.setCreditStatus(assessment.getCreditStatus());
        clientItem.setOwingQuestion(assessment.getOwingQuestion());
        if (assessment.getCriminalQuestions() != null) {
            clientItem.setPendingLegalCaseQuestion(assessment.getCriminalQuestions().getPendingLegalCaseQuestion());
            clientItem.setCriminalConvictionsQuestion(assessment.getCriminalQuestions().getCriminalConvictionsQuestion());
            clientItem.setOpenLegalCaseQuestion(assessment.getCriminalQuestions().getOpenLegalCaseQuestion());
            clientItem.setRegistered290Question(assessment.getCriminalQuestions().getRegistered290Question());
        }
        clientItem.setBatheQuestion(assessment.getBatheQuestion());
        clientItem.setDressAndGroomQuestion(assessment.getDressAndGroomQuestion());
        clientItem.setToiletingQuestion(assessment.getToiletingQuestion());
        clientItem.setEatingQuestion(assessment.getEatingQuestion());
        clientItem.setMedicationQuestion(assessment.getMedicationQuestion());
        clientItem.setHousekeepingQuestion(assessment.getHousekeepingQuestion());
        clientItem.setCookingQuestion(assessment.getCookingQuestion());
        clientItem.setLaundryQuestion(assessment.getLaundryQuestion());
        clientItem.setTelephoneQuestion(assessment.getTelephoneQuestion());
        clientItem.setShoppingQuestion(assessment.getShoppingQuestion());
        clientItem.setFinancesQuestion(assessment.getFinancesQuestion());
        clientItem.setTransportationQuestion(assessment.getTransportationQuestion());
        clientItem.setMakeMedicalAppointmentsQuestion(assessment.getMakeMedicalAppointmentsQuestion());
        clientItem.setMobilityQuestion(assessment.getMobilityQuestion());
        clientItem.setFamilyMemberQuestion(assessment.getFamilyMemberQuestion());
    }

    @Override
    public ReportType getReportType() {
        return ReportType.HOUSING_ASSESSMENT;
    }

    private HousingAssessment parseHousingAssessment(String rawHousingAssessment) {
        try {
            return objectMapper.readValue(rawHousingAssessment, HousingAssessment.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private interface HousingAssessmentReportDetailsAware extends ClientCommunityNameAware, ClientIdNamesAware, DateCompletedAware, StringResultAware {

    }
}
