import InitialState from './ServiceInitialState'

import listReducer from './list/serviceListReducer'
import statusReducer from './status/serviceStatusReducer'
import controlReducer from './control/serviceControlReducer'

const initialState = new InitialState()

export default function serviceReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const status = statusReducer(state.status, action)
    if (status !== state.status) nextState = nextState.setIn(['status'], status)

    const control = controlReducer(state.control, action)
    if (control !== state.control) nextState = nextState.setIn(['control'], control)

    return nextState
}
