import Request from './request/transpRideRequestInitialState'
import History from './history/TranspRideHistoryInitialState'

const { Record } = require('immutable')

export default Record({
    request: Request(),
    history: History()
})