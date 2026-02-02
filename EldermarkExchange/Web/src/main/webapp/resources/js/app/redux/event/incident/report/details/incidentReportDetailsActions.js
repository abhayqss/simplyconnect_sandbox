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
                    type: types.CLEAN_INCIDENT_REPORT_DETAILS
                }
            },
            cleanError: function () {
                return {
                    type: types.CLEAN_INCIDENT_REPORT_DETAILS_ERROR
                }
            },
            load: function (reportId) {
                return function (dispatch) {
                    dispatch({ type: types.LOAD_INCIDENT_REPORT_DETAILS_REQUEST });
                    return service.findById(reportId).then(function (response) {
                        dispatch({ type: types.LOAD_INCIDENT_REPORT_DETAILS_SUCCESS, payload: response.data });
                        return response.data
                    }).catch(function (e) {
                        dispatch({ type: types.LOAD_INCIDENT_REPORT_DETAILS_FAILURE, payload: e });
                    })
                }
            }
        }
    }
);