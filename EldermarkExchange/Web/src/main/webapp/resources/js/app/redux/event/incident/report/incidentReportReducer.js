define(
    [
        path('./IncidentReportInitialState'),
        
        path('./can/incidentReportCanReducer'),
        path('./form/incidentReportFormReducer'),
        path('./details/incidentReportDetailsReducer'),
        path('./initialized/incidentReportInitializedReducer')
    ],
    function (InitialState, canReducer, formReducer, detailsReducer, initializedReducer) {
        return function (state, action) {
            state = state || new InitialState();

            var nextState = state;

            var can = canReducer(state.can, action);
            if (can !== state.can) nextState = nextState.setIn(['can'], can);
            
            var form = formReducer(state.form, action);
            if (form !== state.form) nextState = nextState.setIn(['form'], form);

            var details = detailsReducer(state.details, action);
            if (details !== state.details) nextState = nextState.setIn(['details'], details);

            var initialized = initializedReducer(state.initialized, action);
            if (initialized !== state.initialized) nextState = nextState.setIn(['initialized'], initialized);

            return nextState;
        }
    }
);