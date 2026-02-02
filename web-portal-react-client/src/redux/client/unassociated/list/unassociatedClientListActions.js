import { Actions } from 'redux/utils/List'
import service from 'services/ClientService'

import actionTypes from './unassociatedClientListActionTypes'

export default Actions({
    actionTypes,
    isPageable: false,
    isSortable: false,
    doLoad: (params) => service.findUnassociated(params)
})
