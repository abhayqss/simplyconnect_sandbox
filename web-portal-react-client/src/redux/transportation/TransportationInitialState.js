import Ride from './ride/TransportationRideInitialState'

const { Record } = require('immutable')

export default Record({
    ride: Ride()
})