import { Reducer } from 'redux/utils/List'

import actionTypes from './labOrderListActionTypes'
import InitialState from './LabOrderListInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })