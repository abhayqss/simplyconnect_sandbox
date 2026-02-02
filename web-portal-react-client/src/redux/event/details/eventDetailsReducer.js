import { Reducer } from 'redux/utils/Details'

import actionTypes from './actionTypes'
import InitialState from './EventDetailsInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})