define(
    [
        'Immutable',
        path('./diagnosis/PatientDiagnosisInitialState'),
        path('./medication/PatientMedicationInitialState'),
    ],
    function (Immutable, Diagnosis, Medication) {
        return Immutable.Record({
            diagnosis: Diagnosis(),
            medication: Medication(),
        });
    }
);