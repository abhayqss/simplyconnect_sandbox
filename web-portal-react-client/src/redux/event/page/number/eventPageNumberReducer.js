import { Reducer } from 'redux/utils/Value'

import actionTypes from './actionTypes'
import InitialState from './EventPageNumberInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})