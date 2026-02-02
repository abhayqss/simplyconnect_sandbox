import Factory from '../ActionFactory'

import actions from 'redux/event/community/list/communityListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})