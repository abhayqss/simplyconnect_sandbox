import { Reducer } from 'redux/utils/Value'

import actionTypes from './canAddCategoriesActionTypes'
import InitialState from './CanAddCategoriesInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })