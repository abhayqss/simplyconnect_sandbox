import { Reducer } from 'redux/utils/Value'

import actionTypes from './canViewCategoriesActionTypes'
import InitialState from './CanViewCategoriesInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })