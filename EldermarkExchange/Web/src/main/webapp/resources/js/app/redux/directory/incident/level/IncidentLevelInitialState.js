define(
    ['Immutable', path('./reporting/IncidentLevelReportingInitialState')],
    function (Immutable, ReportingInitialState) {
        return Immutable.Record({
            reporting: ReportingInitialState()
        });
    }
);
