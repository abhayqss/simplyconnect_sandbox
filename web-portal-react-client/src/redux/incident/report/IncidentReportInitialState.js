import Can from './can/CanIncidentReportInitialState'
import List from './list/IncidentReportListInitialState'
import Count from './count/IncidentReportCountInitialState'
import Oldest from './oldest/OldestIncidentReportInitialState'
import Latest from './latest/LatestIncidentReportInitialState'
import Details from './details/IncidentReportDetailsInitialState'
import Deletion from './deletion/IncidentReportDeletionInitialState'
import Conversation from './conversation/IncidentReportConversationInitialState'

import Community from './community/CommunityInitialState'

const { Record } = require('immutable')

export default Record({
    can: Can(),
    list: List(),
    count: Count(),
    oldest: Oldest(),
    latest: Latest(),
    details: Details(),
    deletion: Deletion(),
    community: Community(),
    conversation: Conversation(),
})