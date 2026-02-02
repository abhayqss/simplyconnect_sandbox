exports.ListState = new Immutable.Record({
    error: null,
    isFetching: false,
    dataSource: new Immutable.Record({
        data: null
    })
})