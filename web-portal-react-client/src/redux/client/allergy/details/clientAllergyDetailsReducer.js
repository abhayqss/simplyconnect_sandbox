import { Reducer } from 'redux/utils/Details'

import actionTypes from './clientAllergyDetailsActionTypes'
import InitialState from './ClientAllergyDetailsInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})