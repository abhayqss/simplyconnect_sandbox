import { Actions } from 'redux/utils/List'

import actionTypes from './releaseNoteListActionTypes'

import service from 'services/ReleaseNoteService'

export default Actions({
    actionTypes,
    doLoad: params => service.find(params)
})