import { Actions } from 'redux/utils/List'

import service from 'services/DirectoryService'

import actionTypes from './incidentPlaceListActionTypes'

export default Actions({
    actionTypes,
    isMinimal: true,
    doLoad: params => service.findIncidentPlaces(params)
})