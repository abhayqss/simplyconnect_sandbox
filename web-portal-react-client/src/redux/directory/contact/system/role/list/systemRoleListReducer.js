import { Reducer } from 'redux/utils/List'

import actionTypes from './actionTypes'
import InitialState from './SystemRoleListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,    
    stateClass: InitialState
})