define([path('app/lib/Constants')], function (constants) {
    var types = constants.actionTypes;

    return {
        CLEAN: types.CLEAN_INCIDENT_PLACES,
        CLEAN_ERROR: types.CLEAN_INCIDENT_PLACE_LIST_ERROR,
        LOAD_REQUEST: types.LOAD_INCIDENT_PLACES_REQUEST,
        LOAD_SUCCESS: types.LOAD_INCIDENT_PLACES_SUCCESS,
        LOAD_FAILURE: types.LOAD_INCIDENT_PLACES_FAILURE
    }
});

