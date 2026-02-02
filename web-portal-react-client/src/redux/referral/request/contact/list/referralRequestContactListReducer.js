import { Reducer } from 'redux/utils/List'

import actionTypes from './referralRequestContactListActionTypes'
import InitialState from './ReferralRequestContactListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})