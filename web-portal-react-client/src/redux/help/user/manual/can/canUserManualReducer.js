import InitialState from './CanUserManualInitialState'

import uploadReducer from './upload/canUploadUserManualReducer'
import deletionReducer from './deletion/canDeleteUserManualReducer'

const initialState = new InitialState()

export default function canReferralRequestReducer(state = initialState, action) {
    let nextState = state

    const upload = uploadReducer(state.upload, action)
    if (upload !== state.upload) nextState = nextState.setIn(['upload'], upload)

    const deletion = deletionReducer(state.deletion, action)
    if (deletion !== state.deletion) nextState = nextState.setIn(['deletion'], deletion)

    return nextState
}