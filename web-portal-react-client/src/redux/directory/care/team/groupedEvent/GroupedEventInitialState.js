import Type from './type/GroupedEventTypeInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    type: new Type(),
})

export default InitialState