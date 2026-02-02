import { Reducer } from 'redux/utils/Value'

import actionTypes from './canConfigureCommunityActionTypes'
import InitialState from './CanConfigureCommunityInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })