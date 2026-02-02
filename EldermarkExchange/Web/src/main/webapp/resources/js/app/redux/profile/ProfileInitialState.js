define(
    [
        'Immutable',
        path('./details/ProfileDetailsInitialState')
    ],
    function (Immutable, ProfileDetailsInitialState) {
        return Immutable.Record({
            details: new ProfileDetailsInitialState()
        })
    }
);