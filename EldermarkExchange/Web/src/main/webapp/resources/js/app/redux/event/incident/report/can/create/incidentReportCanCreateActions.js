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
                    type: types.CLEAN_INCIDENT_REPORT_CAN_CREATE
                }
            },
            cleanError: function () {
                return {
                    type: types.CLEAN_INCIDENT_REPORT_CAN_CREATE_ERROR
                }
            },
            load: function () {
                return function (dispatch) {
                    dispatch({ type: types.LOAD_INCIDENT_REPORT_CAN_CREATE_REQUEST });
                    return service.canCreate().then(function (response) {
                        dispatch({ type: types.LOAD_INCIDENT_REPORT_CAN_CREATE_SUCCESS, payload: response.data });
                        return response.data
                    }).catch(function (e) {
                        dispatch({ type: types.LOAD_INCIDENT_REPORT_CAN_CREATE_FAILURE, payload: e });
                    })
                }
            }
        }
    }
);