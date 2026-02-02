import Factory from '../ActionFactory'

import actions from 'redux/client/event/not-viewable/type/list/notViewableEventTypeListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})