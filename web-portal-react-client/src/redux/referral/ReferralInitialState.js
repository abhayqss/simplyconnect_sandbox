import List from './list/ReferralListInitialState'
import Details from './details/ReferralDetailsInitialState'

import Info from './info/ReferralInfoInitialState'
import Community from './community/CommunityInitialState'
import Request from './request/ReferralRequestInitialState'
import Recipient from './recipient/ReferralRecipientInitialState'

const { Record } = require('immutable')

export default Record({
    list: List(),
    info: Info(),
    details: Details(),
    request: Request(),
    community: Community(),
    recipient: Recipient()
})