import { Actions } from 'redux/utils/List'

import actionTypes from './auditLogListActionTypes'

export default Actions({
    actionTypes,
    isPageable: false,
    isSortable: false,
    isFilterable: true
})
