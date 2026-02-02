import listReducer from './list/incidentReportClassMemberTypeListReducer'

import InitialState from './IncidentReportClassMemberTypeInitialState'

const initialState = InitialState()

export default function labOrderReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}