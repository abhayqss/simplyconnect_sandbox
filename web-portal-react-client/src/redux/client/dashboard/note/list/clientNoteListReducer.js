import { Reducer } from 'redux/utils/List'

import actionTypes from './clientNoteListActionTypes'
import stateClass from './ClientNoteListInitialState'

export default Reducer({
    actionTypes,
    stateClass,
    isSortable: false,
    isFilterable: false
})