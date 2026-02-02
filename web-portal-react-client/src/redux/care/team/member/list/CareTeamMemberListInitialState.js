const { Record } = require('immutable')

export default Record({
    error: null,
    fetchCount: 0,
    isFetching: false,
    shouldReload: false,
    dataSource: Record({
        data: [],
        sorting: Record({
            field: 'contactName',
            order: 'asc'
        })(),
        pagination: Record({
            page: 1,
            size: 15,
            totalCount: 0
        })(),
        filter: Record({
            name: ''
        })()
    })()
})

