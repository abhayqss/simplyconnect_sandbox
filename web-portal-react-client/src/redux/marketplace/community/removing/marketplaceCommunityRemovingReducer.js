import { Reducer } from 'redux/utils/Send'

import actionTypes from './marketplaceCommunityRemovingActionTypes'
import InitialState from './MarketplaceCommunityRemovingInitialState'

export default Reducer({
    actionTypes,
    stateClass: InitialState
})