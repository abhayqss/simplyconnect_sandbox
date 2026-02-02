import Type from './type/NotViewableEventTypeInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    type: Type()
})

export default InitialState