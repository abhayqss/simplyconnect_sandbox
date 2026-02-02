import detailsReducer from './details/releaseNotificationDetailsReducer'

import InitialState from './ReleaseNotificationInitialState'

const initialState = InitialState()

export default function releaseNotificationReducer(state = initialState, action) {
    let nextState = state

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    return nextState
}