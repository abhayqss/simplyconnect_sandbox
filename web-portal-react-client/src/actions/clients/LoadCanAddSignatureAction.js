import Factory from '../ActionFactory'

import actions from 'redux/client/can/addSignature/canAddSignatureActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})