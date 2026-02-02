import { Reducer } from 'redux/utils/Details'

import actionTypes from './sendSDoHReportActionTypes'
import InitialState from './sendSDoHReportInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})