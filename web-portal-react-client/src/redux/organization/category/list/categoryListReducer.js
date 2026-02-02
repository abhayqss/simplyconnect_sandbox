import { Reducer } from 'redux/utils/List'

import actionTypes from './actionTypes'
import InitialState from './CategoryListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})