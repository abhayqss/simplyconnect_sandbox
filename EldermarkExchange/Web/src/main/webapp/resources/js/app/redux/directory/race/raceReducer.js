define(
    [
        path('./RaceInitialState'),
        path('./list/raceListReducer')
    ],
    function (InitialState, listReducer) {
        return function (state, action) {
            state = state || new InitialState();

            var nextState = state;

            var list = listReducer(state.list, action);
            if (list !== state.list) nextState = state.setIn(['list'], list);

            return nextState;
        }
    }
);