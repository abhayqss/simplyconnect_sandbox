import rideReducer from './ride/transportationRideReducer'

import InitialState from './TransportationInitialState'

const initialState = InitialState()

export default function labOrderReducer(state = initialState, action) {
    let nextState = state

    const ride = rideReducer(state.ride, action)
    if (ride !== state.ride) nextState = nextState.setIn(['ride'], ride)
    
    return nextState
}