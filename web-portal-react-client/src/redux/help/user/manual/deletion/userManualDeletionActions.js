import { Actions } from 'redux/utils/Delete'

import service from 'services/UserManualService'

import actionTypes from './userManualDeletionActionTypes'

export default Actions({
    actionTypes,
    doDelete: (manualId) => service.deleteById(manualId)
})