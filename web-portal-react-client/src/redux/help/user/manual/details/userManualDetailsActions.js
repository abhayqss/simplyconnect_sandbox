import { Actions } from 'redux/utils/Details'

import service from 'services/UserManualService'

import actionTypes from './userManualDetailsActionTypes'

export default Actions({
    actionTypes,
    doDownload: (manualId, params) => service.downloadById(manualId, params)
})