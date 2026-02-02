define(
    [
        'Immutable',
        path('./list/PatientActiveMedicationListInitialState'),
    ],
    function (Immutable, List) {
        return Immutable.Record({
            list: List()
        });
    }
);