import Factory from '../ActionFactory'

import * as actions from 'redux/directory/care/team/groupedEvent/type/list/groupedEventTypeListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})
