define(
    ['Immutable', path('./type/ClassMemberTypeInitialState')],
    function (Immutable, TypeInitialState) {
        return Immutable.Record({
            type: TypeInitialState()
        });
    }
);