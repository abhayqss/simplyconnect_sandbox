import OrganizationInitialState from './IrInitialState'

import canReducer from './can/canIrReducer'
import detailsReducer from './details/irDetailsReducer'

const initialState = new OrganizationInitialState()

export default function irReducer(state = initialState, action) {
    let nextState = state

    const can = canReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    return nextState
}