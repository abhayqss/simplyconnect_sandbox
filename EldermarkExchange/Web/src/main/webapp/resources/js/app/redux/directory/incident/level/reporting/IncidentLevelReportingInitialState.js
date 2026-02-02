define(
    ['Immutable', path('../../../utils/DetailsStateFactory')],
    function (Immutable, Factory) {
        return Immutable.Record({
            settings: Factory.getStateInstance()
        });
    }
);
