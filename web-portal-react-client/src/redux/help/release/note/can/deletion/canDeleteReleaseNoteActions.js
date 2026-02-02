import { Actions } from 'redux/utils/Value'

import actionTypes from './canDeleteReleaseNoteActionTypes'

import service from 'services/ReleaseNoteService'

export default Actions({
    actionTypes,
    doLoad: () => service.canDelete()
})