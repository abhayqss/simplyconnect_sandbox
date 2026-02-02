import { Actions } from 'redux/utils/Details'

import actionTypes from './actionTypes'

import service from 'services/EventNoteService'

export default Actions({
    actionTypes,
    doLoad: params => service.canAddNote(params)
})