define(
    [
        path('app/lib/Constants'),
        path('./IncidentReportFormInitialState')
    ],
    function (constants, InitialState) {
        var types = constants.actionTypes;

        return function (state, action) {
            var initialState = new InitialState();

            state = state || initialState;

            if (!(state instanceof InitialState)) {
                return initialState.mergeDeep(state)
            }

            switch (action.type) {
                case types.CLEAN_INCIDENT_REPORT_ERROR:
                    return state.removeIn(['error']);

                case types.SAVE_INCIDENT_REPORT_REQUEST: {
                    return state.setIn(['isFetching'], true);
                }

                case types.SAVE_INCIDENT_REPORT_SUCCESS: {
                    return state.setIn(['isFetching'], false);
                }

                case types.SAVE_INCIDENT_REPORT_FAILURE: {
                    return state.setIn(['error'], action.payload)
                        .setIn(['isFetching'], false);
                }
            }

            return state;
        }
    }
);