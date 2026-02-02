import manualReducer from './manual/userManualReducer'

import InitialState from './UserInitialState'

const initialState = InitialState()

export default function userReducer(state = initialState, action) {
    let nextState = state

    const manual = manualReducer(state.manual, action)
    if (manual !== state.manual) nextState = nextState.setIn(['manual'], manual)

    return nextState
}