define(
    [
        path('./PatientMedicationInitialState'),
        path('./active/patientActiveMedicationReducer'),
        path('./inactive/patientInactiveMedicationReducer'),
    ],
    function (InitialState, activeReducer, inactiveReducer) {
        return function (state, action) {
            state = state || new InitialState();

            var nextState = state;

            var active = activeReducer(state.active, action);
            if (active !== state.active) nextState = nextState.setIn(['active'], active);

            var inactive = inactiveReducer(state.inactive, action);
            if (inactive !== state.inactive) nextState = nextState.setIn(['inactive'], inactive);

            return nextState;
        }
    }
);