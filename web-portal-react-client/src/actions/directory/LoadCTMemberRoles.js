import Factory from '../ActionFactory'

import * as actions from 'redux/directory/care/team/role/list/careTeamRoleListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})
