import InitialState from './CanIncidentReportInitialState'

import addReducer from './add/canAddIncidentReportReducer'
import viewReducer from './view/canViewIncidentReportsReducer'

const initialState = new InitialState()

export default function canReferralRequestReducer(state = initialState, action) {
    let nextState = state

    const add = addReducer(state.add, action)
    if (add !== state.add) nextState = nextState.setIn(['add'], add)
    
    const view = viewReducer(state.view, action)
    if (view !== state.view) nextState = nextState.setIn(['view'], view)

    return nextState
}