define(['Immutable'], function (Immutable) {
    return Immutable.Record({
        error: null,
        isFetching: false,
        dataSource: Immutable.Record({
            data: null,
            filter: Immutable.Record({
                patientId: null
            })(),
            pagination: Immutable.Record({
                page: 0,
                pageSize: 10,
                totalCount: 0
            })()
        })()
    });
});