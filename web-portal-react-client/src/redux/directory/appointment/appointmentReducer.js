import InitialState from './AppointmentInitialState'

import typeReducer from './type/appointmentTypeReducer'
import statusReducer from './status/appointmentStatusReducer'

const initialState = new InitialState()

export default function appointmentReducer (state = initialState, action) {
    let nextState = state

    const type = typeReducer(state.type, action)
    if (type !== state.type) nextState = nextState.setIn(['type'], type)
    
    const status = statusReducer(state.status, action)
    if (status !== state.status) nextState = nextState.setIn(['status'], status)

    return nextState
}
