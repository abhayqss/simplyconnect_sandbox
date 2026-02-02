import { Actions } from 'redux/utils/Details'

import service from 'services/ReleaseNoteService'

import actionTypes from './releaseNoteDetailsActionTypes'

export default Actions({
    actionTypes,
    doLoad: (noteId) => service.findById(noteId),
    doDownload: (noteId, params) => service.downloadById(noteId, params)
})