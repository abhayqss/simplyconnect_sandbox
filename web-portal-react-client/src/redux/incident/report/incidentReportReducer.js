import canReducer from './can/canIncidentReportReducer'
import listReducer from './list/incidentReportListReducer'
import countReducer from './count/incidentReportCountReducer'
import oldestReducer from './oldest/oldestIncidentReportReducer'
import latestReducer from './latest/latestIncidentReportReducer'
import detailsReducer from './details/incidentReportDetailsReducer'
import deletionReducer from './deletion/incidentReportDeletionReducer'
import conversationReducer from './conversation/incidentReportConversationReducer'

import communityReducer from './community/communityReducer'

import InitialState from './IncidentReportInitialState'

const initialState = InitialState()

export default function labOrderReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const count = countReducer(state.count, action)
    if (count !== state.count) nextState = nextState.setIn(['count'], count)

    const can = canReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    const oldest = oldestReducer(state.oldest, action)
    if (oldest !== state.oldest) nextState = nextState.setIn(['oldest'], oldest)

    const latest = latestReducer(state.latest, action)
    if (latest !== state.latest) nextState = nextState.setIn(['latest'], latest)

    const deletion = deletionReducer(state.deletion, action)
    if (deletion !== state.deletion) nextState = nextState.setIn(['deletion'], deletion)

    const community = communityReducer(state.community, action)
    if (community !== state.community) nextState = nextState.setIn(['community'], community)
    
    const conversation = conversationReducer(state.conversation, action)
    if (conversation !== state.conversation) nextState = nextState.setIn(['conversation'], conversation)

    return nextState
}