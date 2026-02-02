import InitialState from './AppointmentStatusInitialState'

import listReducer from './list/appointmentStatusListReducer'

const initialState = new InitialState()

export default function appointmentStatusReducer (state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}
