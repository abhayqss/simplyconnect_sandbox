const { Record } = require('immutable')

export default Record({
    error: null,
    shouldReload: true,
    isFetching: false,
    dataSource: Record({
        data: [],
    })()
})

