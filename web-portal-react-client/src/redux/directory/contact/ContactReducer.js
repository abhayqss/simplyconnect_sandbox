import InitialState from './ContactInitialState'

import listReducer from './list/contactListReducer'
import systemReducer from './system/systemReducer'
import statusReducer from './status/ContactStatusReducer'

const initialState = new InitialState()

export default function contactReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)
    
    const system = systemReducer(state.system, action)
    if (system !== state.system) nextState = nextState.setIn(['system'], system)
    
    const status = statusReducer(state.status, action)
    if (status !== state.status) nextState = nextState.setIn(['status'], status)

    return nextState
}
