define(
    [
        path('./IncidentReportCanInitialState'),
        path('./create/incidentReportCanCreateReducer')
    ],
    function (InitialState, createReducer) {
        return function (state, action) {
            state = state || InitialState();

            var nextState = state;

            var create = createReducer(state.create, action);
            if (create !== state.create) nextState = nextState.setIn(['create'], create);

            return nextState;
        }
    }
);