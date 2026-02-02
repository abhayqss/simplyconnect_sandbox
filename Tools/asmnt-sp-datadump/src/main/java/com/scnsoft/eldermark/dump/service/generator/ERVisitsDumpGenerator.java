package com.scnsoft.eldermark.dump.service.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.dump.bean.DumpFilter;
import com.scnsoft.eldermark.dump.dao.ClientAssessmentResultDao;
import com.scnsoft.eldermark.dump.dao.EventDao;
import com.scnsoft.eldermark.dump.entity.EventTypeEnum;
import com.scnsoft.eldermark.dump.model.*;
import com.scnsoft.eldermark.dump.specification.ClientAssessmentResultSpecificationGenerator;
import com.scnsoft.eldermark.dump.specification.EventSpecificationGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
public class ERVisitsDumpGenerator implements DumpGenerator {

    @Autowired
    private EventSpecificationGenerator eventSpecificationGenerator;

    @Autowired
    private EventDao eventDao;

    @Autowired
    private ClientAssessmentResultDao assessmentResultDao;

    @Autowired
    private ClientAssessmentResultSpecificationGenerator assessmentResultSpecificationGenerator;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public List<Dump> generateDump(DumpFilter filter) {
        var report = new ERVisitsDump();

        var rows = new ArrayList<>(eventRows(filter));
        rows.addAll(caRows(filter));

        rows.sort(Comparator.comparing(HospitalizationEventRow::getClientName));

        report.setEventRows(rows);
        return Collections.singletonList(report);
    }

    private List<ERVisitsRow> eventRows(DumpFilter filter) {
        var erVisitType = eventSpecificationGenerator.byEventType(EventTypeEnum.ERV);
        var betweenDates = eventSpecificationGenerator.betweenDates(filter.getFromAtDefaultZone(), filter.getToAtDefaultZone());
        return eventDao.findAll(eventSpecificationGenerator.byClientOrganizationId(filter.getOrganizationId()).and(betweenDates).and(erVisitType))
                .stream()
                .map(event -> {
                    var eventRow = new ERVisitsRow();
                    eventRow.setCommunityName(event.getClient().getCommunity().getName());
                    eventRow.setClientId(event.getClient().getId());
                    eventRow.setClientName(event.getClient().getFullName());
                    eventRow.setDateOfInstitutionalization(event.getEventDateTime());
                    eventRow.setLocation(event.getLocation());
                    eventRow.setSituation(event.getSituation());
                    eventRow.setBackground(event.getBackground());
                    eventRow.setAssessment(event.getAssessment());
                    eventRow.setInjury(event.isInjury());
                    eventRow.setFollowup(event.isFollowup());
                    eventRow.setSource(HospitalizationEventRow.Source.EVENT);
                    return eventRow;
                }).collect(toList());
    }


    private List<ERVisitsRow> caRows(DumpFilter filter) {
        var ca = assessmentResultSpecificationGenerator.comprehensiveType();
        var within = assessmentResultSpecificationGenerator.withinReportPeriod(filter.getFrom(), filter.getTo());
        var latest = assessmentResultSpecificationGenerator.leaveLatest(filter.getTo());
        var inOrg = assessmentResultSpecificationGenerator.ofOrganizationId(filter.getOrganizationId());

        return assessmentResultDao.findAll(ca.and(within).and(latest).and(inOrg)).stream()
                .flatMap(assessment -> {
                    var parsed = DumpGeneratorUtils.parseComprehensive(assessment.getResult(), mapper);
                    var lastEdVisitOpt = DumpGeneratorUtils.parseAssessmentDate(parsed.getLastEDVisitDate());
                    if (lastEdVisitOpt
                            .filter(l -> !l.isBefore(filter.getFromAtDefaultZone()) && !l.isAfter(filter.getToAtDefaultZone()))
                            .isEmpty()) {
                        return Stream.empty();
                    }

                    var eventRow = new ERVisitsRow();
                    eventRow.setCommunityName(assessment.getClient().getCommunity().getName());
                    eventRow.setClientId(assessment.getClient().getId());
                    eventRow.setClientName(assessment.getClient().getFullName());
                    eventRow.setDateOfInstitutionalization(lastEdVisitOpt.get());
                    eventRow.setSituation(parsed.getMedicalHistoryAdmissionsComment());
                    eventRow.setNumberOfEdVisits(parsed.getNumberOfEdVisits6Months());
                    eventRow.setSource(HospitalizationEventRow.Source.CA);
                    return Stream.of(eventRow);
                })
                .collect(Collectors.toList());
    }

    @Override
    public DumpType getDumpType() {
        return DumpType.ER_VISITS;
    }
}
