import { Reducer } from 'redux/utils/Value'

import actionTypes from './clientAllergyCountActionTypes'
import InitialState from './ClientAllergyCountInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})