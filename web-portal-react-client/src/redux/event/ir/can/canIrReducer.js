import InitialState from './CanIrInitialState'

import viewReducer from './view/canViewIrReducer'

const initialState = new InitialState()

export default function canIrReducer(state = initialState, action) {
    let nextState = state

    const view = viewReducer(state.view, action)
    if (view !== state.view) nextState = nextState.setIn(['view'], view)

    return nextState
}