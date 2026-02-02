define(
    [
        'Immutable',
        path('../utils/ListStateFactory'),
        path('./type/IncidentTypeInitialState'),
        path('./place/IncidentPlaceInitialState'),
        path('./level/IncidentLevelInitialState')
    ],
    function (Immutable, Factory, TypeInitialState, PlaceInitialState, LevelInitialState) {
        return Immutable.Record({
            type: TypeInitialState(),
            place: PlaceInitialState(),
            level: LevelInitialState()
        });
    }
);
