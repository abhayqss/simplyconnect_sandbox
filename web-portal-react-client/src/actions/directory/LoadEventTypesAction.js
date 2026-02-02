import Factory from '../ActionFactory'

import actions from 'redux/directory/event/type/list/eventTypeListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load()
})