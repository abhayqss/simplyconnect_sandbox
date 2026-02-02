import Factory from '../ActionFactory'

import actions from 'redux/note/can/add/canAddNoteActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})