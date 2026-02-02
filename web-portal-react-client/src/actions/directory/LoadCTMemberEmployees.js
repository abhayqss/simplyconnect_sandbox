import Factory from '../ActionFactory'

import * as actions from 'redux/directory/care/team/employee/list/careTeamEmployeeListActions'

export default Factory(actions, {
    action: (params, actions) => {
        return actions.load(params)
    }
})
