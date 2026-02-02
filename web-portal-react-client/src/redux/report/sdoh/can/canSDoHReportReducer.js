import CanLabInitialState from './CanSDoHReportInitialState'

import viewReducer from './view/canViewSDoHReportsReducer'
import markAsSentReducer from './mark-as-sent/canMarkAsSentSDoHReportReducer'

const initialState = new CanLabInitialState()

export default function canSDoHReportReducer(state = initialState, action) {
    let nextState = state

    const view = viewReducer(state.view, action)
    if (view !== state.view) nextState = nextState.setIn(['view'], view)
    
    const markAsSent = markAsSentReducer(state.markAsSent, action)
    if (markAsSent !== state.markAsSent) nextState = nextState.setIn(['markAsSent'], markAsSent)

    return nextState
}