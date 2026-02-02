define(
    [
        path('app/lib/Constants'),
        path('app/services/IncidentReportService')
    ], function (constants, IncidentReportService) {
        var types = constants.actionTypes;

        var service = new IncidentReportService();

        return {
            clean: function () {
                return {
                    type: types.CLEAN_INCIDENT_REPORT_INITIALIZED
                }
            },
            cleanError: function () {
                return {
                    type: types.CLEAN_INCIDENT_REPORT_INITIALIZED_ERROR
                }
            },
            load: function (eventId) {
                return function (dispatch) {
                    dispatch({ type: types.LOAD_INCIDENT_REPORT_INITIALIZED_REQUEST });
                    return service.getInitialized(eventId).then(function (response) {
                        dispatch({ type: types.LOAD_INCIDENT_REPORT_INITIALIZED_SUCCESS, payload: response.data });
                        return response.data
                    }).catch(function (e) {
                        dispatch({ type: types.LOAD_INCIDENT_REPORT_INITIALIZED_FAILURE, payload: e });
                    })
                }
            }
        }
    }
);