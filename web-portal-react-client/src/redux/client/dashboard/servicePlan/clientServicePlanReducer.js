import InitialState from './ClientServicePlanInitialState'

import detailsReducer from './details/clientServicePlanDetailsReducer'

const initialState = new InitialState()

export default function clientServicePlanReducer(state = initialState, action) {
    let nextState = state

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    return nextState
}