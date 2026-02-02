import InitialState from './ProgramInitialState'

import typeReducer from './type/programTypeReducer'
import subtypeReducer from './subtype/programSubTypeReducer'

const initialState = new InitialState()

export default function programReducer(state = initialState, action) {
    let nextState = state

    const type = typeReducer(state.type, action)
    if (type !== state.type) nextState = nextState.setIn(['type'], type)

    const subtype = subtypeReducer(state.subtype, action)
    if (subtype !== state.subtype) nextState = nextState.setIn(['subtype'], subtype)

    return nextState
}
