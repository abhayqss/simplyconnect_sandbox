import Factory from '../ActionFactory'

import actions from 'redux/care/team/member/organization/list/careTeamMemberContactOrganizationListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})
