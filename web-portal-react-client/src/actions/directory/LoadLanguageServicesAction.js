import Factory from '../ActionFactory'

import * as actions from 'redux/directory/language/service/list/languageServiceListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})