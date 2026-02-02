import Domain from './domain/DomainInitialState'
import Program from './program/ProgramInitialState'
import Priority from './priority/PriorityInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    domain: Domain(),
    program: Program(),
    priority: Priority()
})

export default InitialState