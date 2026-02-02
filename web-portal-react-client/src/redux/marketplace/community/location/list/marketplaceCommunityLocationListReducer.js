import { Reducer } from 'redux/utils/List'

import actionTypes from './marketplaceCommunityLocationListActionTypes'
import InitialState from './MarketplaceCommunityLocationListInitialState'

export default Reducer({
    actionTypes,
    stateClass: InitialState,
    isMinimal: true
})
