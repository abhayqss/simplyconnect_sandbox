define([path('app/lib/Constants')], function (constants) {
    var types = constants.actionTypes;

    return {
        CLEAN: types.CLEAN_GENDERS,
        CLEAN_ERROR: types.CLEAN_GENDER_LIST_ERROR,
        LOAD_REQUEST: types.LOAD_GENDERS_REQUEST,
        LOAD_SUCCESS: types.LOAD_GENDERS_SUCCESS,
        LOAD_FAILURE: types.LOAD_GENDERS_FAILURE
    }
});