define(
    [
        path('./actionTypes'),
        path('../../../utils/ListStateFactory'),
        path('../../../utils/ListReducerFactory')
    ],
    function (types, StateFactory, ReducerFactory) {
        return ReducerFactory.getReducer({
            actionTypes: types,
            initialState: StateFactory.getStateInstance(),
            initialStateClass: StateFactory.getStateClass()
        });
    }
);