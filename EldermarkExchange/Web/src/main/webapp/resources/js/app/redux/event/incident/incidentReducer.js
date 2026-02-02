define(
    [
        path('./IncidentInitialState'),
        path('./report/incidentReportReducer')
    ],
    function (InitialState, reportReducer) {
        return function (state, action) {
            state = state || new InitialState();

            var nextState = state;

            var report = reportReducer(state.report, action);
            if (report !== state.report) nextState = nextState.setIn(['report'], report);

            return nextState;
        }
    }
);