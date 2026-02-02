define(
    [
        'Immutable',
        path('./list/PatientDiagnosisListInitialState'),
    ],
    function (Immutable, ListInitialState) {
        return Immutable.Record({
            list: ListInitialState()
        });
    }
);