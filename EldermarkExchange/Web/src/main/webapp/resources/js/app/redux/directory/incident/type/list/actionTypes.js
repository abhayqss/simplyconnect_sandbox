define([path('app/lib/Constants')], function (constants) {
  var types = constants.actionTypes;

  return {
      CLEAN: types.CLEAN_INCIDENT_TYPES,
      CLEAN_ERROR: types.CLEAN_INCIDENT_TYPE_LIST_ERROR,
      LOAD_REQUEST: types.LOAD_INCIDENT_TYPES_REQUEST,
      LOAD_SUCCESS: types.LOAD_INCIDENT_TYPES_SUCCESS,
      LOAD_FAILURE: types.LOAD_INCIDENT_TYPES_FAILURE
  }
});
