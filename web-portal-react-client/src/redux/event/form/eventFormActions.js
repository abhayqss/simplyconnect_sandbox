import { Actions } from 'redux/utils/Form'

import actionTypes from './actionTypes'

import service from 'services/EventNoteService'
import validator from 'validators/EventFormValidator'

export default Actions({
    actionTypes,
    doValidate: data => validator.validate(data),
    doSubmit: (...args) => service.saveEvent(...args)
})