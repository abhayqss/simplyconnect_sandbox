import listReducer from './list/incidentTypeListReducer'

import InitialState from './IncidentTypeInitialState'

const initialState = InitialState()

export default function typeReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}