import List from './list/EventTypeListInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    list: List()
})

export default InitialState