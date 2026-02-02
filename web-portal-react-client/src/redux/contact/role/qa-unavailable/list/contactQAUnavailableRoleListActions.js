import { Actions } from 'redux/utils/List'

import actionTypes from './contactQAUnavailableRoleListActionTypes'

import service from 'services/ContactService'

export default Actions({
    actionTypes,
    doLoad: (params, options) => service.findQAUnavailableRoles(params, options)
})