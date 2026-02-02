import { Actions } from 'redux/utils/Value'
import service from 'services/EventNoteService'

import actionTypes from './actionTypes'

export default Actions({
    actionTypes,
    doLoad: params => service.count(params)
})