import { Actions } from 'redux/utils/Value'

import actionTypes from './canUploadUserManualActionTypes'

import service from 'services/UserManualService'

export default Actions({
    actionTypes,
    doLoad: () => service.canUpload()
})