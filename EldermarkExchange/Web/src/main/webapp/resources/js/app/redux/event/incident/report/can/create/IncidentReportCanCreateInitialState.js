define(['Immutable'], function (Immutable) {
    return Immutable.Record({
        error: null,
        isFetching: false,
        value: null
    })
});