import canReducer from './can/canLabResearchOrderReducer'
import listReducer from './list/labOrderListReducer'
import countReducer from './count/labResearchOrderCountReducer'
import reviewReducer from './review/labResearchOrderReviewReducer'
import detailsReducer from './details/labOrderDetailsReducer'
import defaultReducer from './default/labResearchOrderDefaultReducer'

import testReducer from './test/labResearchOrderTestReducer'
import communityReducer from './community/communityReducer'

import InitialState from './LabOrderInitialState'

const initialState = InitialState()

export default function labOrderReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const count = countReducer(state.count, action)
    if (count !== state.count) nextState = nextState.setIn(['count'], count)

    const can = canReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)

    const test = testReducer(state.test, action)
    if (test !== state.test) nextState = nextState.setIn(['test'], test)

    const review = reviewReducer(state.review, action)
    if (review !== state.review) nextState = nextState.setIn(['review'], review)

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    const _default = defaultReducer(state.default, action)
    if (_default !== state._default) nextState = nextState.setIn(['default'], _default)

    const community = communityReducer(state.community, action)
    if (community !== state.community) nextState = nextState.setIn(['community'], community)

    return nextState
}