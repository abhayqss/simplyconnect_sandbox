define(
    [
        path('./actionTypes'),
        path('../../utils/ListActionFactory'),
    ], function (types, Factory) {
        return Factory.getActions({actionTypes: types, entity: 'GENDER'})
    }
);