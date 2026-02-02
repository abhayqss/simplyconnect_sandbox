import { Actions } from 'redux/utils/Details'

import service from 'services/ReleaseNoteService'

import actionTypes from './releaseNotificationDetailsActionTypes'

export default Actions({
    actionTypes,
    doLoad: () => service.findReleaseNotification()
})