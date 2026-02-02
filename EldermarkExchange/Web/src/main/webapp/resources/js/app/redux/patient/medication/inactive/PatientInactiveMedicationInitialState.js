define(
    [
        'Immutable',
        path('./list/PatientInactiveMedicationListInitialState'),
    ],
    function (Immutable, List) {
        return Immutable.Record({
            list: List()
        });
    }
);