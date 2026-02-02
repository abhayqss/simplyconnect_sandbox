import InitialState from './CanAppointmentInitialState'

import addReducer from './add/canAddAppointmentReducer'

const initialState = new InitialState()

export default function canReferralRequestReducer(state = initialState, action) {
    let nextState = state

    const add = addReducer(state.add, action)
    if (add !== state.add) nextState = nextState.setIn(['add'], add)

    return nextState
}