import typeReducer from './type/incidentReportClassMemberTypeReducer'

import InitialState from './IncidentReportClassMemberInitialState'

const initialState = InitialState()

export default function labOrderReducer(state = initialState, action) {
    let nextState = state

    const type = typeReducer(state.type, action)
    if (type !== state.type) nextState = nextState.setIn(['type'], type)

    return nextState
}