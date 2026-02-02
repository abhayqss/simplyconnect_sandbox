import { Actions } from 'redux/utils/Form'

import actionTypes from './actionTypes'

import service from 'services/EventNoteService'

import validator from 'validators/NoteFormValidator'

export default Actions({
    actionTypes,
    doSubmit: (data, params) => service.saveNote(data, params),
    doValidate: (data, options) => validator.validate(data, options)
})