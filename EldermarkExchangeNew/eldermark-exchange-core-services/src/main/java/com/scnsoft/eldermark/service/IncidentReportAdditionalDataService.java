package com.scnsoft.eldermark.service;



import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation;
import com.scnsoft.eldermark.entity.event.incident.IncidentReport;
import com.scnsoft.eldermark.entity.medication.Medication;

import java.util.List;

public interface IncidentReportAdditionalDataService {

    List<ProblemObservation> listProblemObservations(IncidentReport incidentReport);
    List<String> listProblemObservationStrings(IncidentReport incidentReport);

    List<ProblemObservation> listProblemObservations(Long residentId);
    List<String> listProblemObservationStrings(Long residentId);

    List<Medication> listMedications(IncidentReport incidentReport);
    List<String> listMedicationStrings(IncidentReport incidentReport);

    List<Medication> listMedications(Long residentId);
    List<String> listMedicationStrings(Long residentId);

}
