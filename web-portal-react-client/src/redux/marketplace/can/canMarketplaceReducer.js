import OrganizationInitialState from './CanMarketplaceInitialState'

import viewReducer from './view/canViewMarketplaceReducer'

const initialState = new OrganizationInitialState()

export default function canMarketplaceReducer(state = initialState, action) {
    let nextState = state

    const view = viewReducer(state.view, action)
    if (view !== state.view) nextState = nextState.setIn(['view'], view)

    return nextState
}