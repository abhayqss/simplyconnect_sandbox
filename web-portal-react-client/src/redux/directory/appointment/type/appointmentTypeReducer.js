import InitialState from './AppointmentTypeInitialState'

import listReducer from './list/appointmentTypeListReducer'

const initialState = new InitialState()

export default function appointmentTypeReducer (state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}
