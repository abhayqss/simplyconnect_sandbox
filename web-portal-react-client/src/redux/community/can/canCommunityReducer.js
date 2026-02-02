import OrganizationInitialState from './CanCommunityInitialState'

import addReducer from './add/canAddCommunityReducer'
import configureReducer from './configure/canConfigureCommunityReducer'

const initialState = new OrganizationInitialState()

export default function(state = initialState, action) {
    let nextState = state

    const add = addReducer(state.add, action)
    if (add !== state.add) nextState = nextState.setIn(['add'], add)

    const configure = configureReducer(state.configure, action)
    if (configure !== state.configure) nextState = nextState.setIn(['configure'], configure)

    return nextState
}