define(
    [
        path('./IncidentLevelReportingInitialState'),
        path('./settings/incidentLevelReportingSettingsReducer')
    ],
    function (InitialState, settingsReducer) {
        return function (state, action) {
            state = state || InitialState();

            var nextState = state;

            var settings = settingsReducer(state.settings, action);
            if (settings !== state.settings) nextState = state.setIn(['settings'], settings);

            return nextState;
        }
    }
);
