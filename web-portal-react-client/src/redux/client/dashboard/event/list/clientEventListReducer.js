import { Reducer } from 'redux/utils/List'

import actionTypes from './clientEventListActionTypes'
import stateClass from './ClientEventListInitialState'

export default Reducer({
    actionTypes,
    stateClass,
    isSortable: false,
    isFilterable: false
})