package com.scnsoft.eldermark.services;

import com.scnsoft.eldermark.entity.Medication;
import com.scnsoft.eldermark.entity.ProblemObservation;
import com.scnsoft.eldermark.entity.incident.IncidentReport;

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
