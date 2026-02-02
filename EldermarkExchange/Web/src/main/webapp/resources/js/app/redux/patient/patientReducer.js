define(
    [
        path('./PatientInitialState'),
        path('./diagnosis/patientDiagnosisReducer'),
        path('./medication/patientMedicationReducer'),
    ],
    function (InitialState, diagnosisReducer, medicationReducer) {
        return function (state, action) {
            state = state || new InitialState();

            var nextState = state;

            var diagnosis = diagnosisReducer(state.diagnosis, action);
            if (diagnosis !== state.diagnosis) nextState = nextState.setIn(['diagnosis'], diagnosis);

            var medication = medicationReducer(state.medication, action);
            if (medication !== state.medication) nextState = nextState.setIn(['medication'], medication);

            return nextState;
        }
    }
);