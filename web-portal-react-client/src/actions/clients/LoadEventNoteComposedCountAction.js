import Factory from '../ActionFactory'

import actions from 'redux/event/note/composed/count/eventNoteComposedCountActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})