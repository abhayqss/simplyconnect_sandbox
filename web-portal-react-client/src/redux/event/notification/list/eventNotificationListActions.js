import { Actions } from 'redux/utils/List'

import actionTypes from './actionTypes'

import service from 'services/EventNoteService'

export default Actions({
    actionTypes,
    doLoad: params => service.findEventNotifications(params)
})