import { Reducer } from 'redux/utils/List'

import actionTypes from './clientRecordListActionTypes'
import InitialState from './ClientRecordListInitialState'

export default Reducer({
    actionTypes,
    isPageable: false,
    isSortable: false,
    isFilterable: true,
    stateClass: InitialState
})