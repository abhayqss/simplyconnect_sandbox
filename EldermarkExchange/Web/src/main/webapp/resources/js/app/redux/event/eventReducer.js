define(
    [
        path('./EventInitialState'),
        path('./details/eventDetailsReducer'),
        path('./incident/incidentReducer')
    ],
    function (InitialState, detailsReducer, incidentReducer) {
        return function (state, action) {
            state = state || new InitialState();

            var nextState = state;

            var details = detailsReducer(state.details, action);
            if (details !== state.details) nextState = nextState.setIn(['details'], details);

            var incident = incidentReducer(state.incident, action);
            if (incident !== state.incident) nextState = nextState.setIn(['incident'], incident);

            return nextState;
        }
    }
);