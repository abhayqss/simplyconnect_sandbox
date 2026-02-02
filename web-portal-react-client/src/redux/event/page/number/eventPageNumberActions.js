import { Actions } from 'redux/utils/Value'

import actionTypes from './actionTypes'

import service from 'services/EventNoteService'

export default Actions({
    actionTypes,
    doLoad: params => service.findEventPageNumber(params)
})