import orderReducer from './order/labOrderReducer'

import InitialState from './LabResearchInitialState'

const initialState = InitialState()

export default function labResearchReducer(state = initialState, action) {
    let nextState = state

    const order = orderReducer(state.order, action)
    if (order !== state.order) nextState = nextState.setIn(['order'], order)

    return nextState
}