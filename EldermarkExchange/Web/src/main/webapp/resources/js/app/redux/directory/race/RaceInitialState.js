define(
    ['Immutable', path('../utils/ListStateFactory')],
    function (Immutable, Factory) {
        return Immutable.Record({
            list: Factory.getStateInstance()
        });
    }
);