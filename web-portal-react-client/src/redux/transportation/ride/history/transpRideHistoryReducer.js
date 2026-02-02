import { Reducer } from 'redux/utils/Value'

import actionTypes from './transpRideHistoryActionTypes'
import InitialState from './TranspRideHistoryInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })