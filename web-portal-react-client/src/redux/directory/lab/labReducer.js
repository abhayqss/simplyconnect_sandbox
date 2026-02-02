import InitialState from './LabInitialState'

import researchReducer from './research/labResearchReducer'

const initialState = new InitialState()

export default function labReducer(state = initialState, action) {
    let nextState = state
    
    const research = researchReducer(state.research, action)
    if (research !== state.research) nextState = nextState.setIn(['research'], research)

    return nextState
}
