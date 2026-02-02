import statusReducer from './status/incidentReportStatusReducer'
import classMemberReducer from './classMember/incidentReportClassMemberReducer'

import InitialState from './IncidentReportInitialState'

const initialState = InitialState()

export default function labOrderReducer(state = initialState, action) {
    let nextState = state

    const status = statusReducer(state.status, action)
    if (status !== state.status) nextState = nextState.setIn(['status'], status)
    
    const classMember = classMemberReducer(state.classMember, action)
    if (classMember !== state.classMember) nextState = nextState.setIn(['classMember'], classMember)

    return nextState
}