const { Record } = require('immutable')

const Count = Record({
    error: null,
    isFetching: false,
    shouldReload: false,
    fetchCount: 0,
    value: null
})

export default Count
