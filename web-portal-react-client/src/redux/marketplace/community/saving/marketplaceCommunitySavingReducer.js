import { Reducer } from 'redux/utils/Send'

import actionTypes from './marketplaceCommunitySavingActionTypes'
import InitialState from './MarketplaceCommunitySavingInitialState'

export default Reducer({
    actionTypes,
    stateClass: InitialState
})