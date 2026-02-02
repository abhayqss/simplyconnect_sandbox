define(
    [
        path('app/lib/Constants'),
        path('./actionTypes'),
        path('../../../../utils/DetailsActionFactory')
    ], function (constants, actionTypes, Factory) {
        return Factory.getActions({actionTypes: actionTypes, entity: 'INCIDENT_LEVEL_REPORTING_SETTINGS'})
    }
);
