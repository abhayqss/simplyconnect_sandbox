import { Reducer } from 'redux/utils/List'

import actionTypes from './referralRecipientListActionTypes'
import InitialState from './ReferralRecipientListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})