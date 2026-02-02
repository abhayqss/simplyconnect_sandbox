import { State } from 'redux/utils/List'

const { Record, List } = require('immutable')

export default State({
    dataSource: Record({
        data: List([]),
    })()
})