define(
    [
        'underscore',
        path('app/lib/Constants'),
        path('app/services/EventService')
    ],
    function (_, constants, EventService) {
        var types = constants.actionTypes;

        var service = new EventService();

        return {
            clean: function () {
                return { type: types.CLEAN_EVENT_DETAILS }
            },
            cleanError: function () {
                return { type: types.CLEAN_EVENT_DETAILS_ERROR }
            },
            load: function (eventId) {
                return function (dispatch) {
                    dispatch({type: types.LOAD_EVENT_DETAILS_REQUEST});
                    return service.findById(eventId).then(function (data) {
                        dispatch({
                            type: types.LOAD_EVENT_DETAILS_SUCCESS,
                            payload: _.extend({id: eventId}, data)
                        });
                        return data;
                    }).fail(function (e) {
                        dispatch({type: types.LOAD_EVENT_DETAILS_FAILURE, payload: e});
                    })
                }
            }
        };
    }
);