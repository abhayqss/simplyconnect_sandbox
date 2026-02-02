import List from './list/UnassociatedClientListInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    list: List(),
})

export default InitialState