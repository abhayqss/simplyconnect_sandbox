import InitialState from './DocumentInitialState'

import detailsReducer from './details/documentDetailsReducer'

const initialState = new InitialState()

export default function documentReducer(state = initialState, action) {
    let nextState = state

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    return nextState
}