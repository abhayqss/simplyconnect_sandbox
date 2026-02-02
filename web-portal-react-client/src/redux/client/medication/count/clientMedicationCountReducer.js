import { Reducer } from 'redux/utils/Value'

import actionTypes from './clientMedicationCountActionTypes'
import InitialState from './ClientMedicationCountInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})