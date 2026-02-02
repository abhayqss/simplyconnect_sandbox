define(
    [
        path('app/lib/Constants'),
        path('./IncidentReportCanCreateInitialState')
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
                case types.CLEAN_INCIDENT_REPORT_CAN_CREATE: {
                    return state
                        .removeIn(['value'])
                        .removeIn(['error'])
                        .setIn(['isFetching'], false)
                }

                case types.CLEAN_INCIDENT_REPORT_CAN_CREATE_ERROR:
                    return state.removeIn(['error']);

                case types.LOAD_INCIDENT_REPORT_CAN_CREATE_REQUEST: {
                    return state.setIn(['isFetching'], true)
                        .setIn(['error'], null)
                }

                case types.LOAD_INCIDENT_REPORT_CAN_CREATE_SUCCESS: {
                    return state.setIn(['isFetching'], false)
                        .setIn(['value'], action.payload)
                }

                case types.LOAD_INCIDENT_REPORT_CAN_CREATE_FAILURE: {
                    return state.setIn(['error'], action.payload)
                        .setIn(['isFetching'], false);
                }
            }

            return state;
        }
    }
);