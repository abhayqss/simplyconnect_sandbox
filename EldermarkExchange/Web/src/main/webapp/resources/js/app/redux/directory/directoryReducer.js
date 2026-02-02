define(
    [
        path('./DirectoryInitialState'),

        path('./race/raceReducer'),
        path('./state/stateReducer'),
        path('./gender/genderReducer'),
        path('./incident/incidentReducer'),
        path('./classMember/classMemberReducer')
    ],
    function (InitialState, raceReducer, stateReducer, genderReducer, incidentReducer, classMemberReducer) {
        return function (state, action) {
            state = state || new InitialState();

            var nextState = state;

            var race = raceReducer(state.race, action);
            if (race !== state.race) nextState = nextState.setIn(['race'], race);

            var usState = stateReducer(state.state, action);
            if (usState !== state.state) nextState = nextState.setIn(['state'], usState);

            var gender = genderReducer(state.gender, action);
            if (gender !== state.gender) nextState = nextState.setIn(['gender'], gender);

            var incident = incidentReducer(state.incident, action);
            if (incident !== state.incident) nextState = nextState.setIn(['incident'], incident);

            var classMember = classMemberReducer(state.classMember, action);
            if (classMember !== state.classMember) nextState = nextState.setIn(['classMember'], classMember);

            return nextState;
        }
    }
);
