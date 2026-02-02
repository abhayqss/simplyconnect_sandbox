import OrganizationInitialState from './CanReferralRequestInitialState'

import addReducer from './add/canAddReferralRequestReducer'

const initialState = new OrganizationInitialState()

export default function canReferralRequestReducer(state = initialState, action) {
    let nextState = state

    const add = addReducer(state.add, action)
    if (add !== state.add) nextState = nextState.setIn(['add'], add)

    return nextState
}