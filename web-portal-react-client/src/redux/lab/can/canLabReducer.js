import CanLabInitialState from './CanLabInitialState'

import viewReducer from './view/canViewLabReducer'

const initialState = new CanLabInitialState()

export default function canLabReducer(state = initialState, action) {
    let nextState = state

    const view = viewReducer(state.view, action)
    if (view !== state.view) nextState = nextState.setIn(['view'], view)

    return nextState
}