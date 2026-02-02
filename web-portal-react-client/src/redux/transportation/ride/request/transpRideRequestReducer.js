import { Reducer } from 'redux/utils/Value'

import actionTypes from './transpRideRequestActionTypes'
import InitialState from './transpRideRequestInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })