define(
    [
        path('./PatientActiveMedicationInitialState'),
        path('./list/patientActiveMedicationListReducer')
    ],
    function (InitialState, listReducer) {
        return function (state, action) {
            state = state || new InitialState();

            var nextState = state;

            var list = listReducer(state.list, action);
            if (list !== state.list) nextState = nextState.setIn(['list'], list);

            return nextState;
        }
    }
);