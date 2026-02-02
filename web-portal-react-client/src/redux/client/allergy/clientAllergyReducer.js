import InitialState from './ClientAllergyInitialState'

import listReducer from './list/clientAllergyListReducer'
import countReducer from './count/clientAllergyCountReducer'
import detailsReducer from './details/clientAllergyDetailsReducer'

const initialState = InitialState()

export default function clientAllergyReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const count = countReducer(state.count, action)
    if (count !== state.count) nextState = nextState.setIn(['count'], count)

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    return nextState
}