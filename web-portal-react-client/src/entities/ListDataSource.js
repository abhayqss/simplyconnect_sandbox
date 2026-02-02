const { List, Record } = require('immutable')

const ListDataSource = Record({
    error: null,
    isFetching: false,
    data: List([])
})

export default ListDataSource
