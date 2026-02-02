define(
    [
        'Immutable',
        path('./details/EventDetailsInitialState'),
        path('./incident/IncidentInitialState')
    ],
    function (Immutable, Details, Incident) {
        return Immutable.Record({
            details: new Details(),
            incident: new Incident()
        })
    }
);