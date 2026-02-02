import { Actions } from 'redux/utils/Value'

import actionTypes from './canDeleteUserManualActionTypes'

import service from 'services/UserManualService'

export default Actions({
    actionTypes,
    doLoad: () => service.canDelete()
})