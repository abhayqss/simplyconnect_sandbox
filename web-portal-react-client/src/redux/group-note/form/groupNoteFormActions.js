import { Actions } from 'redux/utils/Form'

import actionTypes from './actionTypes'

import service from 'services/EventNoteService'

import validator from 'validators/GroupNoteFormValidator'

export default Actions({
    actionTypes,
    doSubmit: (data, params) => service.saveNote(data, params),
    doValidate: (data, options) => validator.validate(data, options),
    shouldThrowError: true
})