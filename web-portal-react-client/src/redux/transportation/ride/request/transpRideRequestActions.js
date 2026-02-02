import { Actions } from 'redux/utils/Value'

import actionTypes from './transpRideRequestActionTypes'

import service from 'services/TransportationService'

export default Actions({
    actionTypes,
    doLoad: params => service.rideRequest(params)
})