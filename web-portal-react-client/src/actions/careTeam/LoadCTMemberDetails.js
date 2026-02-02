import Factory from '../ActionFactory'

import * as actions from 'redux/care/team/member/details/careTeamMemberDetailsActions'

export default Factory(actions, {
    action: ({ memberId }, actions) => actions.load(memberId)
})
