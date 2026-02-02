define(
    [
        path('app/lib/Constants'),
        path('./actionTypes'),
        path('../../../utils/ListActionFactory')
    ], function (constants, actionTypes, Factory) {
        return Factory.getActions({actionTypes: actionTypes, entity: 'INCIDENT_PLACE'})
    }
);