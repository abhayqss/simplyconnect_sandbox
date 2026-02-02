import userReducer from './user/userReducer'
import releaseReducer from './release/releaseReducer'

import InitialState from './HelpInitialState'

const initialState = InitialState()

export default function labOrderReducer(state = initialState, action) {
    let nextState = state

    const user = userReducer(state.user, action)
    if (user !== state.user) nextState = nextState.setIn(['user'], user)
    
    const release = releaseReducer(state.release, action)
    if (release !== state.release) nextState = nextState.setIn(['release'], release)

    return nextState
}