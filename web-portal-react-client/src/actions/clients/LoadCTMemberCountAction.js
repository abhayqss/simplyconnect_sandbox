import Factory from '../ActionFactory'

import * as actions from 'redux/client/careTeamMember/count/careTeamMemberCountActions'

export default Factory(actions, {
    action: ({ clientId }, actions) => actions.load({ clientId, affiliation: 'BOTH' })
})
