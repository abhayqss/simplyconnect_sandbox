import { Reducer } from 'redux/utils/List'

import actionTypes from './clientAllergyListActionTypes'
import InitialState from './ClientAllergyListInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})