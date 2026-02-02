import Factory from '../ActionFactory'

import actions from 'redux/event/note/can/add/canAddEventNoteActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})