exports.directoryActions = (function() {
    var service = null;

    ServiceProvider
        .getService('DirectoryService')
        .then(function (srv) {
        service = srv;
    });

    var path = ['directory'];
    var types = exports.constants.actionTypes;

    function setAppState (state) {
        ExchangeApp.setState(state);
    }

    function setInAppState (state) {
        return ExchangeApp.state.setIn(path, state)
    }

    function getInAppState () {
        return ExchangeApp.state.getIn(path)
    }

    function reducer (action) {
        return exports.directoryReducer(getInAppState(), action);
    }

    return {
        loadGenders: function () {
            if (service) {
                setAppState(setInAppState(reducer({type: types.LOAD_GENDERS_REQUEST})));
                return service.findGenders()
                    .then(function (data) {
                        setAppState(setInAppState(reducer({type: types.LOAD_GENDERS_SUCCESS, payload: data})));
                    }).catch(function (e) {
                        setAppState(setInAppState(reducer({type: types.LOAD_GENDERS_ERROR, payload: e})));
                    });
            }
            throw new Error('DirectoryService is not loaded');
        },
        loadRaces: function () {
            if (service) {
                setAppState(setInAppState(reducer({type: types.LOAD_RACES_REQUEST})));
                return service.findRaces()
                    .then(function (data) {
                        setAppState(setInAppState(reducer({type: types.LOAD_RACES_SUCCESS, payload: data})));
                    }).catch(function (e) {
                        setAppState(setInAppState(reducer({type: types.LOAD_RACES_ERROR, payload: e})));
                    });
            }
            throw new Error('DirectoryService is not loaded');
        }
    }
})();