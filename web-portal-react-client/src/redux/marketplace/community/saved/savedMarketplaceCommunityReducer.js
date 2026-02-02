import InitialState from './SavedMarketplaceCommunityInitialState'

import listReducer from './list/savedMarketplaceCommunityListReducer'

const initialState = new InitialState()

export default function savedMarketplaceCommunityReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}