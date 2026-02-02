package com.scnsoft.eldermark.service;


import com.scnsoft.eldermark.dao.IncidentReportDao;
import com.scnsoft.eldermark.dao.MedicationDao;
import com.scnsoft.eldermark.dao.ProblemObservationDao;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation;
import com.scnsoft.eldermark.entity.event.incident.IncidentReport;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.entity.medication.MedicationInformation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class IncidentReportAdditionalDataServiceImpl implements IncidentReportAdditionalDataService {

    private static final String STRING_VALUES_SEPARATOR = ", ";
    private static final List<String> INACTIVE_PROBLEM_STATUSES = Arrays.asList("inactive", "resolved");

    @Autowired
    private ProblemObservationDao problemObservationDao;
    @Autowired
    private MedicationDao medicationDao;

    @Autowired
    private ClientService clientService;
    @Autowired
    private IncidentReportDao incidentReportDao;


    @Override
    public List<ProblemObservation> listProblemObservations(IncidentReport incidentReport) {
        return listProblemObservations(incidentReport.getEvent().getClient().getId(), resolveFirstSubmitDate(incidentReport));
    }

    @Override
    public List<String> listProblemObservationStrings(IncidentReport incidentReport) {
        return listProblemObservationStrings(listProblemObservations(incidentReport));
    }

    @Override
    public List<ProblemObservation> listProblemObservations(Long clientId) {
        return listProblemObservations(clientId, null);
    }

    @Override
    public List<String> listProblemObservationStrings(Long clientId) {
        return listProblemObservationStrings(listProblemObservations(clientId));

    }

    private List<ProblemObservation> listProblemObservations(Long clientId, Date firstSubmitDate) {
        return problemObservationDao.listResidentProblemsWithoutDuplicates(clientService.findAllMergedClientsIds(clientId), true, true, true)
                .stream()
                .filter(Objects::nonNull)
                .filter(problem -> isActiveProblemObservation(problem.getProblemStatusText())
                        && hasStartDateBeforeIrSubmitDate(firstSubmitDate, problem.getProblemDateTimeLow()))
                .collect(Collectors.toList());
    }

    private boolean isActiveProblemObservation(String problemStatusText) {
        return StringUtils.isNotEmpty(problemStatusText) && !INACTIVE_PROBLEM_STATUSES.contains(problemStatusText.toLowerCase());
    }

    private List<String> listProblemObservationStrings(List<ProblemObservation> problemObservations) {
        List<String> stringList = new ArrayList<>();
        for (ProblemObservation problemObservation : problemObservations) {
            CcdCode problemCode = findBestCodeWithIcd10Priority(problemObservation);

            final String problemObservationText = StringUtils.join(new String[]{
                    problemCode == null ? problemObservation.getProblemName() : problemCode.getDisplayName(),
                    problemCode == null ? problemObservation.getProblemIcdCode() : problemCode.getCode(),
                    problemCode == null ? problemObservation.getProblemIcdCodeSet() : problemCode.getCodeSystemName()
            }, STRING_VALUES_SEPARATOR);
            if (StringUtils.isNotEmpty(problemObservationText)) {
                stringList.add(problemObservationText);
            }
        }
        return stringList;
    }

    private CcdCode findBestCodeWithIcd10Priority(ProblemObservation problemObservation) {
        final List<CcdCode> problemCodes = new ArrayList<>();
        if (problemObservation.getProblemCode() != null) {
            problemCodes.add(problemObservation.getProblemCode());
        }
        problemCodes.addAll(problemObservation.getTranslations());

        if (problemCodes.isEmpty()) {
            return null;
        }

        for (CcdCode ccdCode : problemCodes) {
            if (ProblemService.ICD_10_CM_CODE_SYSTEM_CODE.equals(ccdCode.getCodeSystem())) {
                return ccdCode;
            }
        }
        return problemCodes.get(0);
    }

    @Override
    public List<Medication> listMedications(IncidentReport incidentReport) {
        return listMedications(incidentReport.getEvent().getClient().getId(), resolveFirstSubmitDate(incidentReport));
    }

    @Override
    public List<String> listMedicationStrings(IncidentReport incidentReport) {
        return listMedicationStrings(listMedications(incidentReport));
    }

    private Date resolveFirstSubmitDate(IncidentReport incidentReport) {
        if (incidentReport.getChainId() == null) {
            if (incidentReport.getSubmitted()) {
                return Date.from(incidentReport.getLastModifiedDate());
            }
            return null;
        }
        IncidentReport firstSubmitIr = incidentReportDao.findFirstByChainIdAndSubmittedIsTrueOrderById(incidentReport.getChainId());

        return firstSubmitIr == null ? null : Date.from(firstSubmitIr.getLastModifiedDate());
    }

    @Override
    public List<Medication> listMedications(Long residentId) {
        return listMedications(residentId, null);
    }

    @Override
    public List<String> listMedicationStrings(Long residentId) {
        return listMedicationStrings(listMedications(residentId));
    }

    private List<Medication> listMedications(Long clientId, Date firstSubmitDate) {
        return medicationDao.listResidentMedicationsWithoutDuplicates(clientService.findAllMergedClientsIds(clientId), true, true)
                .stream()
                .filter(medication ->
                        medication.getStatusCode() != null
                                && medication.getStatusCode().equalsIgnoreCase("active")
                                && hasStartDateBeforeIrSubmitDate(firstSubmitDate, medication.getMedicationStarted()))
                .collect(Collectors.toList());
    }

    private boolean hasStartDateBeforeIrSubmitDate(Date firstSubmitDate, Date startDate) {
        return startDate != null &&
                (firstSubmitDate == null || startDate.before(firstSubmitDate));
    }

    private List<String> listMedicationStrings(List<Medication> medications) {
        return  medications.stream()
                .map(medication -> Stream.of(Optional.ofNullable(medication.getMedicationInformation()).map(MedicationInformation::getProductNameText).orElse(null),
                        Optional.ofNullable(medication.getDoseQuantity()).map(String::valueOf).orElse(null),
                        medication.getFreeTextSig())
                        .filter(Objects::nonNull).collect(Collectors.joining(STRING_VALUES_SEPARATOR)))
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
    }

}
