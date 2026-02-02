import ProspectInitialState from './ProspectInitialState'

import routeReducer from './route/prospectRouteReducer'

const initialState = new ProspectInitialState()

export default function prospectReducer(state = initialState, action) {
    let nextState = state

    const route = routeReducer(state.route, action)
    if (route !== state.route) nextState = nextState.setIn(['route'], route)

    return nextState
}