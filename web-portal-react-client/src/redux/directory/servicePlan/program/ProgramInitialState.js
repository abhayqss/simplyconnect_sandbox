import Type from './type/ProgramTypeInitialState'
import SubType from './subtype/ProgramSubTypeInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    type: new Type(),
    subtype: new SubType()
})

export default InitialState