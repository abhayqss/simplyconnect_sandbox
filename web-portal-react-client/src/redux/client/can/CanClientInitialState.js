import Add from './add/CanAddClientInitialState'
import Edit from './edit/CanEditClientInitialState'
import CanAddSignatureInitialState from './addSignature/CanAddSignatureInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    add: Add(),
    edit: Edit(),
    addSignature: CanAddSignatureInitialState()
})

export default InitialState
