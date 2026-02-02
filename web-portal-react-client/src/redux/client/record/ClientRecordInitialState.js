import List from './list/ClientRecordListInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    list: List()
})

export default InitialState