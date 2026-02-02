import requestReducer from './request/transpRideRequestReducer'
import historyReducer from './history/transpRideHistoryReducer'

import InitialState from './TransportationRideInitialState'

const initialState = InitialState()

export default function labOrderReducer(state = initialState, action) {
    let nextState = state

    const request = requestReducer(state.request, action)
    if (request !== state.request) nextState = nextState.setIn(['request'], request)
    
    const history = historyReducer(state.history, action)
    if (history !== state.history) nextState = nextState.setIn(['history'], history)

    return nextState
}