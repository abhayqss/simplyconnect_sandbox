package com.scnsoft.eldermark.hl7v2.processor.problem;

import com.scnsoft.eldermark.beans.ValueSetEnum;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.Problem;
import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation;
import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.entity.xds.message.EVNSegmentContainingMessage;
import com.scnsoft.eldermark.entity.xds.segment.AdtDG1DiagnosisSegment;
import com.scnsoft.eldermark.entity.xds.segment.EVNEventTypeSegment;
import com.scnsoft.eldermark.hl7v2.processor.CcdCodeResolverService;
import com.scnsoft.eldermark.hl7v2.processor.DataTypeUtils;
import com.scnsoft.eldermark.service.CcdCodeService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Transactional
class HL7v2ProblemFactoryImpl implements HL7v2ProblemFactory {
    private static final Logger logger = LoggerFactory.getLogger(HL7v2ProblemFactoryImpl.class);

    final static String DIAGNOSIS_TYPE_CODE = "282291009";

    @Autowired
    private CcdCodeService ccdCodeService;

    @Autowired
    private CcdCodeResolverService ccdCodeResolverService;


    @Override
    public Optional<Problem> createProblem(Client client, AdtDG1DiagnosisSegment dg1, AdtMessage adtMessage) {
        if (!containsRequiredData(dg1)) {
            return Optional.empty();
        }

        var problem = new Problem();

        problem.setLegacyId(0);
        problem.setOrganization(client.getOrganization());
        problem.setOrganizationId(client.getOrganizationId());
        problem.setClient(client);
        problem.setClientId(client.getId());

        var lowTime = resolveLowTime(dg1, adtMessage);
        problem.setTimeLow(lowTime);

        problem.setDg1Id(dg1.getId());

        problem.setProblemObservations(List.of(createProblemObservation(dg1, lowTime, client.getOrganization())));
        problem.getProblemObservations().forEach(po -> po.setProblem(problem));

        return Optional.of(problem);
    }

    private boolean containsRequiredData(AdtDG1DiagnosisSegment dg1) {
        return !DataTypeUtils.isEmpty(dg1.getDiagnosisCode());
    }

    private Date resolveLowTime(AdtDG1DiagnosisSegment dg1, AdtMessage adtMessage) {
        return Optional.ofNullable(dg1.getDiagnosisDateTime())
                .or(() -> {
                    if (adtMessage instanceof EVNSegmentContainingMessage) {
                        return Optional.ofNullable(((EVNSegmentContainingMessage) adtMessage).getEvn())
                                .map(EVNEventTypeSegment::getEventOccurred);
                    }
                    return Optional.empty();
                })
                .or(() -> Optional.ofNullable(adtMessage.getMsh().getDatetime()))
                .map(Date::from)
                .orElseGet(Date::new);
    }

    private ProblemObservation createProblemObservation(AdtDG1DiagnosisSegment dg1, Date lowTime, Organization organization) {
        var observation = new ProblemObservation();

        observation.setLegacyId(0);
        observation.setOrganization(organization);
        observation.setOrganizationId(organization.getId());
        observation.setNegationInd(false);
        observation.setManual(false);

        observation.setRecordedDate(lowTime);
        observation.setProblemDateTimeLow(lowTime);

        processDiagnosisCode(observation, dg1.getDiagnosisCode());

        observation.setProblemType(ccdCodeService.findByCodeAndValueSet(DIAGNOSIS_TYPE_CODE, ValueSetEnum.PROBLEM_TYPE_2006));

        return observation;
    }

    private void processDiagnosisCode(ProblemObservation observation, CECodedElement diagnosisCode) {
        var ccdDiagnosisCode = ccdCodeResolverService.resolveCode(diagnosisCode);
        var problemName = Stream.concat(Stream.of(diagnosisCode)
                                .filter(Objects::nonNull)
                                .flatMap(c -> Stream.of(c.getText(), c.getAlternateText())),
                        ccdDiagnosisCode.map(CcdCode::getDisplayName)
                                .stream()
                ).filter(StringUtils::isNotEmpty)
                .findFirst()
                .orElse(null);

        observation.setProblemCode(ccdDiagnosisCode.orElse(null));
        observation.setProblemName(problemName);

        Optional.ofNullable(diagnosisCode)
                .ifPresent(ce -> {
                    observation.setProblemIcdCode(ce.getIdentifier());
                    //todo check real data
                    observation.setProblemIcdCodeSet(ce.getNameOfCodingSystem());
                });
    }
}
