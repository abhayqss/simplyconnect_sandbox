import { Actions } from 'redux/utils/Value'

import actionTypes from './canUploadReleaseNoteActionTypes'

import service from 'services/ReleaseNoteService'

export default Actions({
    actionTypes,
    doLoad: () => service.canUpload()
})