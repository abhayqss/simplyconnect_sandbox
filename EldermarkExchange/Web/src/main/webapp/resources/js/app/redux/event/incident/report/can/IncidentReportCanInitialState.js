define(
    [
        'Immutable',
        path('./create/IncidentReportCanCreateInitialState')
    ],
    function (Immutable, Create) {
        return Immutable.Record({
            create: Create()
        })
    }
);