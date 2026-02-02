import Status from './status/ReferralStatusInitialState'
import Priority from './priority/ReferralPriorityInitialState'
import Intent from './intent/ReferralIntentInitialState'
import Reason from './reason/ReferralReasonInitialState'
import Category from './category/ReferralCategoryInitialState'
import Network from './network/ReferralNetworkInitialState'
import Decline from './decline/ReferralDeclineInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    status: Status(),
    priority: Priority(),
    intent: Intent(),
    reason: Reason(),
    category: Category(),
    network: Network(),
    decline: Decline(),
})

export default InitialState