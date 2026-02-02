import List from './list/ReferralInfoRequestListInitialState'
import Details from './details/ReferralInfoRequestDetailsInitialState'

const { Record } = require('immutable')

export default Record({
    list: List(),
    details: Details()
})