import { Actions } from 'redux/utils/Value'

import actionTypes from './transpRideHistoryActionTypes'

import service from 'services/TransportationService'

export default Actions({
    actionTypes,
    doLoad: params => service.rideHistory(params)
})