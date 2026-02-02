import Factory from '../ActionFactory'

import * as actions from 'redux/directory/care/team/responsibility/list/careTeamResponsibilityListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load()
})
