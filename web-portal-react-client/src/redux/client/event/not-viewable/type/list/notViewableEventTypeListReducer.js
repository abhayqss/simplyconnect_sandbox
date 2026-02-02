import { Reducer } from 'redux/utils/List'

import actionTypes from './actionTypes'
import InitialState from './NotViewableEventTypeListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,    
    stateClass: InitialState
})