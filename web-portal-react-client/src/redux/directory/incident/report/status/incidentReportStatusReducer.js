import listReducer from './list/incidentReportStatusListReducer'

import InitialState from './IncidentReportStatusInitialState'

const initialState = InitialState()

export default function labOrderReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}