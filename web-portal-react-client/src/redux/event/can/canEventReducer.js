import OrganizationInitialState from './CanEventInitialState'

import addReducer from './add/canAddEventReducer'

const initialState = new OrganizationInitialState()

export default function canEventReducer(state = initialState, action) {
    let nextState = state

    const add = addReducer(state.add, action)
    if (add !== state.add) nextState = nextState.setIn(['add'], add)

    return nextState
}