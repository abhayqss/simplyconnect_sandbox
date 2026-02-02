import Can from './can/CanMarketplaceInitialState'
import Community from './community/MarketplaceCommunityInitialState'

const { Record } = require('immutable')

export default Record({
    can: Can(),
    community: Community()
})
