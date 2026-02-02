define(
    [
        'Immutable',
        path('./race/RaceInitialState'),
        path('./state/StateInitialState'),
        path('./gender/GenderInitialState'),
        path('./incident/IncidentInitialState'),
        path('./classMember/ClassMemberInitialState')
    ],
    function (Immutable, Race, State, Gender, Incident, ClassMember) {
        return Immutable.Record({
            race: Race(),
            state: State(),
            gender: Gender(),
            incident: Incident(),
            classMember: ClassMember()
        })
    }
);