import InitialState from './MarketplaceInitialState'

import canReducer from './can/canMarketplaceReducer'
import communityReducer from './community/marketplaceCommunityReducer'

const initialState = new InitialState()

export default function marketplaceReducer(state = initialState, action) {
    let nextState = state

    const can = canReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)

    const community = communityReducer(state.community, action)
    if (community !== state.community) nextState = nextState.setIn(['community'], community)

    return nextState
}
