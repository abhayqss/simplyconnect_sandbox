import InitialState from './ReferralCategoryInitialState'

import listReducer from './list/referralCategoryListReducer'

const initialState = new InitialState()

export default function referralCategoryReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}
