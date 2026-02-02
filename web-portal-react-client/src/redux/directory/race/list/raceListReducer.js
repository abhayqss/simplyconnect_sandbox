import { Reducer } from 'redux/utils/List'

import actionTypes from './raceListActionTypes'
import InitialState from './RaceListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,    
    stateClass: InitialState
})