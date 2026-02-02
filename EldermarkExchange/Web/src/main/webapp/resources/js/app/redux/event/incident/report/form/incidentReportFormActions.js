define(
    [
        'underscore',
        path('app/lib/Constants'),
        path('app/services/IncidentReportService')
    ],
    function (_, constants, IncidentReportService) {
        var types = constants.actionTypes;

        var service = new IncidentReportService();

        return {
            cleanError: function () {
                return { type: types.CLEAN_EVENT_DETAILS_ERROR }
            },
            save: function (eventId, report) {
                return function (dispatch) {
                    dispatch({type: types.SAVE_INCIDENT_REPORT_REQUEST});
                    return service.save(eventId, report).then(function (response) {
                        dispatch({
                            type: types.SAVE_INCIDENT_REPORT_SUCCESS,
                            payload: response.data
                        });
                        return response.data;
                    }).catch(function (e) {
                        dispatch({type: types.SAVE_INCIDENT_REPORT_FAILURE, payload: e});
                    })
                }
            },
            saveDraft: function (eventId, report) {
                return function (dispatch) {
                    dispatch({type: types.SAVE_INCIDENT_REPORT_DRAFT_REQUEST});
                    return service.saveDraft(eventId, report).then(function (response) {
                        dispatch({
                            type: types.SAVE_INCIDENT_REPORT_DRAFT_SUCCESS,
                            payload: response.data
                        });
                        return response.data;
                    }).catch(function (e) {
                        dispatch({type: types.SAVE_INCIDENT_REPORT_DRAFT_FAILURE, payload: e});
                    })
                }
            }
        };
    }
);