define([path('app/lib/Constants')], function (constants) {
    var types = constants.actionTypes;

    return {
        CLEAN: types.CLEAN_RACES,
        CLEAN_ERROR: types.CLEAN_RACE_LIST_ERROR,
        LOAD_REQUEST: types.LOAD_RACES_REQUEST,
        LOAD_SUCCESS: types.LOAD_RACES_SUCCESS,
        LOAD_FAILURE: types.LOAD_RACES_FAILURE
    }
});