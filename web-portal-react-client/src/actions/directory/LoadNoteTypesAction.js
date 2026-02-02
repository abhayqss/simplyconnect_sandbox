import Factory from '../ActionFactory'

import actions from 'redux/directory/note/type/list/noteTypeListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load()
})