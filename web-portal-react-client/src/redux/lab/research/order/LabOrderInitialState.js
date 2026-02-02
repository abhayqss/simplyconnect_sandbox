import List from './list/LabOrderListInitialState'
import Can from './can/CanLabResearchOrderInitialState'
import Count from './count/LabResearchOrderCountInitialState'
import Details from './details/LabOrderDetailsInitialState'
import Default from './default/LabResearchOrderDefaultInitialState'

import Test from './test/LabResearchOrderTestInitialState'
import Community from './community/CommunityInitialState'
import Review from './review/LabResearchOrderReviewInitialState'

const { Record } = require('immutable')

export default Record({
    can: Can(),
    list: List(),
    test: Test(),
    count: Count(),
    review: Review(),
    details: Details(),
    default: Default(),
    community: Community()
})