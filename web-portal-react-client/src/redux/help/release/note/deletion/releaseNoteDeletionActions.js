import { Actions } from 'redux/utils/Delete'

import service from 'services/ReleaseNoteService'

import actionTypes from './releaseNoteDeletionActionTypes'

export default Actions({
    actionTypes,
    doDelete: (noteId) => service.deleteById(noteId)
})