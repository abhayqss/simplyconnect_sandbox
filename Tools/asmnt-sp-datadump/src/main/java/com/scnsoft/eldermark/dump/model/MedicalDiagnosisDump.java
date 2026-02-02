package com.scnsoft.eldermark.dump.model;

import org.springframework.data.util.Pair;

import java.util.List;

public class MedicalDiagnosisDump extends Dump {

    List<Pair<String, List<MedicalDiagnosisInfo>>> medicalDiagnosisByCommunityNames;

    public List<Pair<String, List<MedicalDiagnosisInfo>>> getMedicalDiagnosisByCommunityNames() {
        return medicalDiagnosisByCommunityNames;
    }

    public void setMedicalDiagnosisByCommunityNames(List<Pair<String, List<MedicalDiagnosisInfo>>> medicalDiagnosisByCommunityNames) {
        this.medicalDiagnosisByCommunityNames = medicalDiagnosisByCommunityNames;
    }

    @Override
    public DumpType getDumpType() {
        return DumpType.MEDICAL_DIAGNOSIS;
    }
}
