import Factory from '../ActionFactory'

import * as actions from 'redux/directory/care/team/notification/preference/list/careTeamNotificationPreferenceListActions'

export default Factory(actions, {
    action: ({ careTeamRoleId }, actions) => actions.load({ careTeamRoleId })
})
