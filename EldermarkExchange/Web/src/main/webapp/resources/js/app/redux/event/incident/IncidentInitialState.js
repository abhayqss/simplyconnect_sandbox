define(
    [
        'Immutable',
        path('./report/IncidentReportInitialState')
    ],
    function (Immutable, Report) {
        return Immutable.Record({
            report: new Report()
        })
    }
);