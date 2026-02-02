import actions from 'redux/event/details/eventDetailsActions'

import Factory from '../ActionFactory'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})