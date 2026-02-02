import Can from './can/CanSDoHReportInitialState'
import List from './list/SDoHReportListInitialState'
import Send from './send/sendSDoHReportInitialState'
import Community from './community/CommunityInitialState'
import Details from './details/SDoHReportDetailsInitialState'

const { Record } = require('immutable')

export default Record({
    can: Can(),
    list: List(),
    send: Send(),
    details: Details(),
    community: Community()
})