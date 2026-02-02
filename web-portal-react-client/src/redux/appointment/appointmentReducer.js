import InitialState from './AppointmentInitialState'

import canReducer from './can/canAppointmentReducer'
import listReducer from './list/appointmentListReducer'
import communityReducer from './community/communityReducer'
import exportReducer from './export/appointmentExportReducer'

const initialState = new InitialState()

export default function appointmentReducer(state = initialState, action) {
    let nextState = state

    const can = canReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)
    
    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)
    
    const community = communityReducer(state.community, action)
    if (community !== state.community) nextState = nextState.setIn(['community'], community)

    const exporting = exportReducer(state.export, action)
    if (exporting !== state.export) nextState = nextState.setIn(['export'], exporting)

    return nextState
}