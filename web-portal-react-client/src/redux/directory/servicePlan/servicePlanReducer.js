import InitialState from './ServicePlanInitialState'

import domainReducer from './domain/domainReducer'
import programReducer from './program/programReducer'
import priorityReducer from './priority/priorityReducer'

const initialState = new InitialState()

export default function needReducer(state = initialState, action) {
    let nextState = state

    const domain = domainReducer(state.domain, action)
    if (domain !== state.domain) nextState = nextState.setIn(['domain'], domain)

    const program = programReducer(state.program, action)
    if (program !== state.program) nextState = nextState.setIn(['program'], program)

    const priority = priorityReducer(state.priority, action)
    if (priority !== state.priority) nextState = nextState.setIn(['priority'], priority)

    return nextState
}
