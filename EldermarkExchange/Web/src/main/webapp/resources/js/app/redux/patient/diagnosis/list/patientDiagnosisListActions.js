define(
    [
        path('app/lib/Constants'),
        path('app/services/DiagnosisService')
    ],
    function (constants, DiagnosisService) {
        var types = constants.actionTypes;
        var service = new DiagnosisService();

        return {
            clean: function () {
                return { type: types.CLEAN_PATIENT_DIAGNOSES }
            },
            cleanError: function () {
                return { type: types.CLEAN_PATIENT_DIAGNOSIS_LIST_ERROR }
            },
            load: function (params) {
                return function (dispatch) {
                    dispatch({type: types.LOAD_PATIENT_DIAGNOSES_REQUEST});
                    return service.find(params).then(function (response) {
                        dispatch({
                            type: types.LOAD_PATIENT_DIAGNOSES_SUCCESS,
                            payload: $.extend({ data: response.data }, params),
                        });
                        return response.data;
                    }).catch(function (e) {
                        dispatch({type: types.LOAD_PATIENT_DIAGNOSES_FAILURE, payload: e});
                    })
                }
            }
        };
    }
);