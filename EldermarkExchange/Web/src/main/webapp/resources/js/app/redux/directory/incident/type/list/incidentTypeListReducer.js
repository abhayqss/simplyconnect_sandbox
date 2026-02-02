define(
    [
        path('./actionTypes'),
        path('../../../utils/ListStateFactory'),
        path('../../../utils/ListReducerFactory')
    ], function (actionTypes, StateFactory, ReducerFactory) {
        return ReducerFactory.getReducer({
            actionTypes: actionTypes,
            initialState: StateFactory.getStateInstance(),
            initialStateClass: StateFactory.getStateClass()
        })
    }
);
