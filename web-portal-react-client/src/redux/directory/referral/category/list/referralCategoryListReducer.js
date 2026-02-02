import { Reducer } from 'redux/utils/List'

import actionTypes from './referralCategoryListActionTypes'
import InitialState from './ReferralCategoryListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,    
    stateClass: InitialState
})