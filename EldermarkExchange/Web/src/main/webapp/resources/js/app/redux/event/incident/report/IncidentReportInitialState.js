define(
    [
        'Immutable',
        path('./can/IncidentReportCanInitialState'),
        path('./form/IncidentReportFormInitialState'),
        path('./details/IncidentReportDetailsInitialState'),
        path('./initialized/IncidentReportInitializedInitialState')
    ],
    function (Immutable, Can, Form, Details, Initialized) {
        return Immutable.Record({
            can: Can(),
            form: Form(),
            details: Details(),
            initialized: Initialized()
        })
    }
);