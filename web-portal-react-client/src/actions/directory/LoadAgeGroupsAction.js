import Factory from '../ActionFactory'

import * as actions from 'redux/directory/age/group/list/ageGroupListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})