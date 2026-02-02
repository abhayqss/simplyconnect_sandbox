import InitialState from './CanClientInitialState'
import canAddReducer from './add/canAddClientReducer'
import canEditReducer from './edit/canEditClientReducer'
import canAddSignatureReducer from './addSignature/canAddSignatureReducer'

const initialState = new InitialState()

export default function(state = initialState, action) {
    let nextState = state

    const add = canAddReducer(state.add, action)
    if (add !== state.add) nextState = nextState.setIn(['add'], add)
    
    const edit = canEditReducer(state.edit, action)
    if (edit !== state.edit) nextState = nextState.setIn(['edit'], edit)

    const addSignature = canAddSignatureReducer(state.addSignature, action)
    if (addSignature !== state.addSignature) nextState = nextState.setIn(['addSignature'], addSignature)

    return nextState
}