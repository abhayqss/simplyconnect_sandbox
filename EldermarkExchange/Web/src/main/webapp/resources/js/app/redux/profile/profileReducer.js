define(
    [
        path('./ProfileInitialState'),
        path('./details/profileDetailsReducer')
    ],
    function (InitialState, detailsReducer) {
        return function (state, action) {
            state = state || new InitialState();

            var nextState = state;

            var details = detailsReducer(state.details, action);
            if (details !== state.details) nextState = nextState.setIn(['details'], details);

            return nextState;
        }
    }
);