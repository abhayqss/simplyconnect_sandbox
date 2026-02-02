define([path('app/services/DirectoryService')], function (Service) {

    function ListActionFactory () {}

    ListActionFactory.getActions = function (opts) {
        var service = new Service();
        var types = opts.actionTypes || {};

        return {
            load: function (params) {
                return function (dispatch) {
                    dispatch({type: types.LOAD_REQUEST});
                    return service.find(opts.entity, params).then(function (resp) {
                        dispatch({type: types.LOAD_SUCCESS, payload: {data: resp.data}});
                        return resp
                    }).fail(function (e) {
                        dispatch({type: types.LOAD_FAILURE, payload: e})
                    });
                }
            },
            clean: function () {
                return {type: types.CLEAN};
            },
            cleanError: function () {
                return {type: types.CLEAN_ERROR};
            }
        }
    };

    return ListActionFactory;
});