const { Record, List } = require('immutable')

const ReferralNetwork = Record({
    id: null,
    communityIds: List()
})

export default ReferralNetwork
