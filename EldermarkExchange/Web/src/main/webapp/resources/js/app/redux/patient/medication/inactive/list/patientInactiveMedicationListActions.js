define(
    [
        path('app/lib/Constants'),
        path('app/services/MedicationService')
    ],
    function (constants, MedicationService) {
        var types = constants.actionTypes;
        var service = new MedicationService();

        return {
            clean: function () {
                return { type: types.CLEAN_PATIENT_INACTIVE_MEDICATIONS }
            },
            cleanError: function () {
                return { type: types.CLEAN_PATIENT_INACTIVE_MEDICATION_LIST_ERROR }
            },
            load: function (params) {
                return function (dispatch) {
                    dispatch({type: types.LOAD_PATIENT_INACTIVE_MEDICATIONS_REQUEST});
                    return service.findInactive(params).then(function (response) {
                        dispatch({
                            type: types.LOAD_PATIENT_INACTIVE_MEDICATIONS_SUCCESS,
                            payload: $.extend({ data: response.data }, params),
                        });
                        return response.data;
                    }).catch(function (e) {
                        dispatch({type: types.LOAD_PATIENT_INACTIVE_MEDICATIONS_FAILURE, payload: e});
                    })
                }
            }
        };
    }
);