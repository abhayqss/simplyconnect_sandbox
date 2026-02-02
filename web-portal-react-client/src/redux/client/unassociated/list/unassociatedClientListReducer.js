import { Reducer } from 'redux/utils/List'

import actionTypes from './unassociatedClientListActionTypes'
import InitialState from './UnassociatedClientListInitialState'

export default Reducer({
    actionTypes,
    isPageable: false,
    isSortable: false,
    stateClass: InitialState
})