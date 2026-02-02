define([], function () {

    function ListReducerFactory () {}

    ListReducerFactory.getReducer = function (opts) {
        var types = opts.actionTypes || {};

        return function genderListReducer (state, action) {
            state = state || opts.initialState;

            if (!(state instanceof opts.initialStateClass)) {
                return opts.initialState.mergeDeep(state);
            }

            switch (action.type) {
                case types.CLEAN:
                    return state.removeIn(['error'])
                        .setIn(['isFetching'], false)
                        .setIn(['shouldReload'], action.payload || false)
                        .removeIn(['dataSource', 'data']);

                case types.CLEAN_ERROR:
                    return state.removeIn(['error']);

                case types.LOAD_REQUEST: {
                    return state.setIn(['error'], null)
                        .setIn(['isFetching'], true)
                        .setIn(['shouldReload'], false);
                }

                case types.LOAD_SUCCESS: {
                    return state.setIn(['isFetching'], false)
                        .setIn(['shouldReload'], false)
                        .setIn(['dataSource', 'data'], action.payload.data);
                }

                case types.LOAD_FAILURE:
                    return state.setIn(['isFetching'], false)
                        .setIn(['shouldReload'], false)
                        .setIn(['error'], action.payload);
            }

            return state;
        }
    };

    return ListReducerFactory;
});