define([path('app/lib/Constants')], function (constants) {
    var types = constants.actionTypes;

    return {
        CLEAN: types.CLEAN_STATES,
        CLEAN_ERROR: types.CLEAN_STATE_LIST_ERROR,
        LOAD_REQUEST: types.LOAD_STATES_REQUEST,
        LOAD_SUCCESS: types.LOAD_STATES_SUCCESS,
        LOAD_FAILURE: types.LOAD_STATES_FAILURE
    }
});