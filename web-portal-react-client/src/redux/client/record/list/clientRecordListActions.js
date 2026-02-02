import { Actions } from 'redux/utils/List'
import service from 'services/ClientService'

import actionTypes from './clientRecordListActionTypes'

export default Actions({
    actionTypes,
    isPageable: false,
    isSortable: false,
    isFilterable: true
})
