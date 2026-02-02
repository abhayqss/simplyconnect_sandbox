define(
    [
        path('./actionTypes'),
        path('../../../../utils/DetailsStateFactory'),
        path('../../../../utils/DetailsReducerFactory')
    ], function (actionTypes, StateFactory, ReducerFactory) {
        return ReducerFactory.getReducer({
            actionTypes: actionTypes,
            initialState: StateFactory.getStateInstance(),
            initialStateClass: StateFactory.getStateClass()
        })
    }
);


