import Factory from '../ActionFactory'

import actions from 'redux/client/document/count/clientDocumentCountActions'

export default Factory(actions, {
    action: ({ clientId }, actions) => actions.load({ clientId })
})
