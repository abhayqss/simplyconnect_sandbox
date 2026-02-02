define(
    [
        path('app/lib/Constants'),
        path('./EventDetailsInitialState')
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
                case types.CLEAN_EVENT_DETAILS:
                    return state
                        .removeIn(['data'])
                        .removeIn(['error'])
                        .setIn(['isFetching'], false);

                case types.CLEAN_EVENT_DETAILS_ERROR:
                    return state.removeIn(['error']);

                case types.LOAD_EVENT_DETAILS_REQUEST:
                    return state
                        .setIn(['isFetching'], true)
                        .setIn(['error'], null);

                case types.LOAD_EVENT_DETAILS_SUCCESS:
                    return state
                        .setIn(['isFetching'], false)
                        .setIn(['data'], action.payload);

                case types.LOAD_EVENT_DETAILS_FAILURE:
                    return state.setIn(['isFetching'], false)
                        .setIn(['error'], action.payload)
            }
            return state;
        }
    }
);