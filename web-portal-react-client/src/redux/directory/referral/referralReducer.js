import InitialState from './ReferralInitialState'

import statusReducer from './status/referralStatusReducer'
import priorityReducer from './priority/referralPriorityReducer'
import intentReducer from './intent/referralIntentReducer'
import reasonReducer from './reason/referralReasonReducer'
import categoryReducer from './category/referralCategoryReducer'
import networkReducer from './network/referralNetworkReducer'
import declineReducer from './decline/referralDeclineReducer'

const initialState = new InitialState()

export default function referralReducer(state = initialState, action) {
    let nextState = state

    const status = statusReducer(state.status, action)
    if (status !== state.status) nextState = nextState.setIn(['status'], status)

    const priority = priorityReducer(state.priority, action)
    if (priority !== state.priority) nextState = nextState.setIn(['priority'], priority)

    const intent = intentReducer(state.intent, action)
    if (intent !== state.intent) nextState = nextState.setIn(['intent'], intent)

    const reason = reasonReducer(state.reason, action)
    if (reason !== state.reason) nextState = nextState.setIn(['reason'], reason)

    const category = categoryReducer(state.category, action)
    if (category !== state.category) nextState = nextState.setIn(['category'], category)

    const network = networkReducer(state.network, action)
    if (network !== state.network) nextState = nextState.setIn(['network'], network)

    const decline = declineReducer(state.decline, action)
    if (decline !== state.decline) nextState = nextState.setIn(['decline'], decline)

    return nextState
}
