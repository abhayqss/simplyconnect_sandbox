import InitialState from './CanClientDocumentInitialState'

import canAddReducer from './add/canAddClientDocumentReducer'

const initialState = new InitialState()

export default function(state = initialState, action) {
    let nextState = state

    const add = canAddReducer(state.add, action)
    if (add !== state.add) nextState = nextState.setIn(['add'], add)

    return nextState
}