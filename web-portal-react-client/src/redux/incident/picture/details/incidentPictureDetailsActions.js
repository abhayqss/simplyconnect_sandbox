import { Actions } from 'redux/utils/Details'

import service from 'services/IncidentReportService'

import actionTypes from './incidentPictureDetailsActionTypes'

export default Actions({
    actionTypes,
    doLoad: (pictureId) => service.findIncidentPictureById(pictureId)
})