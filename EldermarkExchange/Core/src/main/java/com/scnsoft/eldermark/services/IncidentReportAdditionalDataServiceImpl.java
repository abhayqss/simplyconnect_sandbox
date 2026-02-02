package com.scnsoft.eldermark.services;

import com.scnsoft.eldermark.dao.healthdata.MedicationDao;
import com.scnsoft.eldermark.dao.healthdata.ProblemObservationDao;
import com.scnsoft.eldermark.dao.incident.IncidentReportDao;
import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.entity.Medication;
import com.scnsoft.eldermark.entity.ProblemObservation;
import com.scnsoft.eldermark.entity.incident.IncidentReport;
import com.scnsoft.eldermark.services.ccd.section.ProblemService;
import com.scnsoft.eldermark.services.merging.MPIService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IncidentReportAdditionalDataServiceImpl implements IncidentReportAdditionalDataService {

    private static final String STRING_VALUES_SEPARATOR = ", ";
    private static final List<String> INACTIVE_PROBLEM_STATUSES = Arrays.asList("inactive", "resolved");

    private final ProblemObservationDao problemObservationDao;
    private final MedicationDao medicationDao;
    private final MPIService mpiService;
    private final IncidentReportDao incidentReportDao;

    @Autowired
    public IncidentReportAdditionalDataServiceImpl(ProblemObservationDao problemObservationDao, MedicationDao medicationDao, MPIService mpiService, IncidentReportDao incidentReportDao) {
        this.problemObservationDao = problemObservationDao;
        this.medicationDao = medicationDao;
        this.mpiService = mpiService;
        this.incidentReportDao = incidentReportDao;
    }

    @Override
    public List<ProblemObservation> listProblemObservations(IncidentReport incidentReport) {
        return listProblemObservations(incidentReport.getEvent().getResident().getId(), resolveFirstSubmitDate(incidentReport));
    }

    @Override
    public List<String> listProblemObservationStrings(IncidentReport incidentReport) {
        return listProblemObservationStrings(listProblemObservations(incidentReport));
    }

    @Override
    public List<ProblemObservation> listProblemObservations(Long residentId) {
        return listProblemObservations(residentId, null);
    }

    @Override
    public List<String> listProblemObservationStrings(Long residentId) {
        return listProblemObservationStrings(listProblemObservations(residentId));

    }

    private List<ProblemObservation> listProblemObservations(Long residentId, Date firstSubmitDate) {
        final List<Long> residentIds = mpiService.listResidentWithMergedResidents(residentId);
        final List<ProblemObservation> problemObservations = problemObservationDao.listResidentProblemsWithoutDuplicates(residentIds, true, true, true);

        final List<ProblemObservation> result = new ArrayList<>();
        for (ProblemObservation problemObservation : problemObservations) {
            if (problemObservation != null &&
                    isActiveProblemObservation(problemObservation.getProblemStatusText()) &&
                    hasStartDateBeforeIrSubmitDate(firstSubmitDate, problemObservation.getProblemDateTimeLow())) {
                result.add(problemObservation);
            }
        }
        return result;
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

        for (CcdCode ccdCode: problemCodes) {
            if (ProblemService.ICD_10_CM_CODE_SYSTEM_CODE.equals(ccdCode.getCodeSystem())) {
                return ccdCode;
            }
        }
        return problemCodes.get(0);
    }

    @Override
    public List<Medication> listMedications(IncidentReport incidentReport) {
        return listMedications(incidentReport.getEvent().getResident().getId(), resolveFirstSubmitDate(incidentReport));
    }

    @Override
    public List<String> listMedicationStrings(IncidentReport incidentReport) {
        return listMedicationStrings(listMedications(incidentReport));
    }

    private Date resolveFirstSubmitDate(IncidentReport incidentReport) {
        if (incidentReport.getChainId() == null) {
            if (incidentReport.getIsSubmit()) {
                return incidentReport.getLastModifiedDate();
            }
            return null;
        }
        IncidentReport firstSubmitIr = incidentReportDao.findFirstByChainIdAndIsSubmitIsTrueOrderById(incidentReport.getChainId());

        return firstSubmitIr == null ? null : firstSubmitIr.getLastModifiedDate();
    }

    @Override
    public List<Medication> listMedications(Long residentId) {
        return listMedications(residentId, null);
    }

    @Override
    public List<String> listMedicationStrings(Long residentId) {
        return listMedicationStrings(listMedications(residentId));
    }

    private List<Medication> listMedications(Long residentId, Date firstSubmitDate) {
        final List<Long> residentIds = mpiService.listResidentWithMergedResidents(residentId);
        final Collection<Medication> medications = medicationDao.listResidentMedicationsWithoutDuplicates(residentIds, true, true);

        final List<Medication> result = new ArrayList<>();
        for (Medication med : medications) {
            if (med.getStatusCode() != null &&
                    "active".equalsIgnoreCase(med.getStatusCode()) &&
                    hasStartDateBeforeIrSubmitDate(firstSubmitDate, med.getMedicationStarted())) {
                result.add(med);
            }

        }
        return result;
    }

    private boolean hasStartDateBeforeIrSubmitDate(Date firstSubmitDate, Date startDate) {
        return startDate != null &&
                (firstSubmitDate == null || startDate.before(firstSubmitDate));
    }

    private List<String> listMedicationStrings(List<Medication> medications) {
        List<String> medicationStrings = new ArrayList<>();

        for (Medication med : medications) {
            final String medicationString = StringUtils.join(new Object[]{
                    med.getMedicationInformation() != null ? med.getMedicationInformation().getProductNameText() : null,
                    med.getDoseQuantity(),
                    med.getFreeTextSig()
            }, STRING_VALUES_SEPARATOR);
            if (StringUtils.isNotEmpty(medicationString)) {
                medicationStrings.add(medicationString);
            }
        }
        return medicationStrings;
    }

}
