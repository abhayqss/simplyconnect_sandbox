import List from './list/ReferralNetworkListInitialState'
import Details from './details/ReferralNetworkDetailsInitialState'

const { Record } = require('immutable')

export default Record({
    list: List(),
    details: Details(),
})