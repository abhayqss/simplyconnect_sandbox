import InitialState from './LabInitialState'

import canReducer from './can/canLabReducer'
import researchReducer from './research/labResearchReducer'

const initialState = new InitialState()

export default function labReducer(state = initialState, action) {
    let nextState = state

    const can = canReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)

    const research = researchReducer(state.research, action)
    if (research !== state.research) nextState = nextState.setIn(['research'], research)

    return nextState
}