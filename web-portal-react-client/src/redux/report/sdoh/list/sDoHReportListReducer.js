import { Reducer } from 'redux/utils/List'

import actionTypes from './sDoHReportListActionTypes'
import InitialState from './SDoHReportListInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })