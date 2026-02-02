define(
    [
        path('./IncidentInitialState'),
        path('./type/incidentTypeReducer'),
        path('./place/incidentPlaceReducer'),
        path('./level/incidentLevelReducer')
    ],
    function (InitialState, typeReducer, placeReducer, levelReducer) {
        return function (state, action) {
            state = state || new InitialState();

            var nextState = state;

            var type = typeReducer(state.type, action);
            if (type !== state.type) nextState = state.setIn(['type'], type);

            var place = placeReducer(state.place, action);
            if (place !== state.place) nextState = state.setIn(['place'], place);

            var level = levelReducer(state.level, action);
            if (level !== state.level) nextState = state.setIn(['level'], level);

            return nextState;
        }
    }
);
