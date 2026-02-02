import { Actions } from 'redux/utils/Form'

import actionTypes from './releaseNoteFormActionTypes'

import service from 'services/ReleaseNoteService'

export default Actions({
    actionTypes,
    doSubmit: data => service.save(data)
})