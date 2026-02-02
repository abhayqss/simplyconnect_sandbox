import { Reducer } from 'redux/utils/Details'

import actionTypes from './sDoHReportActionTypes'
import InitialState from './SDoHReportDetailsInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})