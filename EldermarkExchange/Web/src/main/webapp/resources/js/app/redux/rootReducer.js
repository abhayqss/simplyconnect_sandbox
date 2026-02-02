define(
    [
        'redux',
        path('./event/eventReducer'),
        path('./profile/profileReducer'),
        path('./patient/patientReducer'),
        path('./directory/directoryReducer')
    ],
    function (redux, eventReducer, profileReducer, patientReducer, directoryReducer) {
        return function () {
            return redux.combineReducers({
                event: eventReducer,
                profile: profileReducer,
                patient: patientReducer,
                directory: directoryReducer
            })
        }
    }
);