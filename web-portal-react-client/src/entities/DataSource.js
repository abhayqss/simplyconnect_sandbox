const { Record } = require('immutable')

const DataSource = Record({
    error: null,
    isFetching: false,
    data: null
})

export default DataSource
