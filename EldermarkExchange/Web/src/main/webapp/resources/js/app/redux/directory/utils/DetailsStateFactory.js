define(['Immutable'], function (Immutable) {
    var State = Immutable.Record ({
        error: null,
        isFetching: false,
        shouldReload: false,
        data: null
    });

    function ListStateFactory () {}

    ListStateFactory.getStateInstance = function () {
        return State();
    };

    ListStateFactory.getStateClass = function () {
        return State;
    };

    return ListStateFactory;
});