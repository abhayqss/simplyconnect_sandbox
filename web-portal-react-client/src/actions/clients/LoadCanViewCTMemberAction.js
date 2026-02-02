import Factory from '../ActionFactory'

import * as actions from 'redux/client/careTeamMember/can/view/canViewCareTeamMemberActions'

export default Factory(actions, {
    action: ({ clientId }, actions) => actions.load({ clientId })
})
