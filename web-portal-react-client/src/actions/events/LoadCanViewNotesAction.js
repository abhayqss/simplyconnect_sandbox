import Factory from '../ActionFactory'

import actions from 'redux/note/can/view/canViewNotesActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})