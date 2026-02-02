import { Actions } from 'redux/utils/List'
import service from 'services/ClientService'

import actionTypes from './clientListActionTypes'

export default Actions({
    actionTypes,
    doLoad: (params, options) => service.find(params, options)
})
