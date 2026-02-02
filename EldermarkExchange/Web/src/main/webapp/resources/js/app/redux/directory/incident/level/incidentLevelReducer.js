define(
    [
        path('./IncidentLevelInitialState'),
        path('./reporting/incidentLevelReportingReducer')
    ],
    function (InitialState, reportingReducer) {
        return function (state, action) {
            state = state || InitialState();

            var nextState = state;

            var reporting = reportingReducer(state.reporting, action);
            if (reporting !== state.reporting) nextState = state.setIn(['reporting'], reporting);

            return nextState;
        }
    }
);
