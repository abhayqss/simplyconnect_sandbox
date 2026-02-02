define(
    [
        'Immutable',
        path('./active/PatientActiveMedicationInitialState'),
        path('./inactive/PatientInactiveMedicationInitialState'),
    ],
    function (Immutable, Active, Inactive) {
        return Immutable.Record({
            active: Active(),
            inactive: Inactive(),
        });
    }
);