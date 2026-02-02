define(
    [
        'underscore',
        path('app/lib/Constants'),
        path('app/services/ProfileService')
    ],
    function (_, constants, ProfileService) {
        var types = constants.actionTypes;

        var service = new ProfileService();

        return {
            clean: function () {
                return { type: types.CLEAN_PROFILE_DETAILS }
            },
            cleanError: function () {
                return { type: types.CLEAN_PROFILE_DETAILS_ERROR }
            },
            load: function () {
                return function (dispatch) {
                    dispatch({type: types.LOAD_PROFILE_DETAILS_REQUEST});
                    return service.find().then(function (data) {
                        dispatch({
                            type: types.LOAD_PROFILE_DETAILS_SUCCESS,
                            payload: data
                        });
                        return data;
                    }).catch(function (e) {
                        dispatch({type: types.LOAD_PROFILE_DETAILS_FAILURE, payload: e});
                    })
                }
            }
        };
    }
);