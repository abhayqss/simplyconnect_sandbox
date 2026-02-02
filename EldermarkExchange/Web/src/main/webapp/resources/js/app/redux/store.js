define(
    [
        'redux',
        'redux-thunk',
        path('./rootReducer'),
        path('./event/EventInitialState'),
        path('./profile/ProfileInitialState'),
        path('./patient/PatientInitialState'),
        path('./directory/DirectoryInitialState')
    ],
    function (redux, reduxThunk, rootReducer, EventInitialState, ProfileInitialState, PatientInitialState, DirectoryInitialState) {

        function getInitialState () {
            return {
                event: new EventInitialState(),
                profile: new ProfileInitialState(),
                patient: new PatientInitialState(),
                directory: new DirectoryInitialState()
            }
        }

        return redux.createStore(
            rootReducer(),
            getInitialState(),
            redux.compose(redux.applyMiddleware(reduxThunk.default))
        );
    }
);