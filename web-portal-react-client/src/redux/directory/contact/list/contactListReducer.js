import { Reducer } from 'redux/utils/List'

import actionTypes from './contactListActionTypes'
import InitialState from './contactListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})