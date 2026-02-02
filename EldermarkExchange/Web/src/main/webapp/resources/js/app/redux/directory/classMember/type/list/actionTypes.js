define([path('app/lib/Constants')], function (constants) {
    var types = constants.actionTypes;

    return {
        CLEAN: types.CLEAN_CLASS_MEMBER_TYPES,
        CLEAN_ERROR: types.CLEAN_CLASS_MEMBER_TYPE_LIST_ERROR,
        LOAD_REQUEST: types.LOAD_CLASS_MEMBER_TYPES_REQUEST,
        LOAD_SUCCESS: types.LOAD_CLASS_MEMBER_TYPES_SUCCESS,
        LOAD_FAILURE: types.LOAD_CLASS_MEMBER_TYPES_FAILURE
    }
});