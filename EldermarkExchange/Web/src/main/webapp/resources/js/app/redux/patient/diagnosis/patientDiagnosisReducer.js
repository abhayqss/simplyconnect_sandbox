define(
    [
        path('./PatientDiagnosisInitialState'),
        path('./list/patientDiagnosisListReducer')
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