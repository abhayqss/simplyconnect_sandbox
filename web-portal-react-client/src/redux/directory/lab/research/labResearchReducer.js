import InitialState from './LabResearchInitialState'

import orderReducer from './order/labResearchOrderReducer'
import reasonReducer from './reason/labResearchReasonReducer'
import icdCodeReducer from './icd-code/labResearchIcdCodeReducer'
import policyHolderRelationReducer from './policy-holder-relation/labResearchPolicyHolderRelationReducer'

const initialState = new InitialState()

export default function labResearchReducer(state = initialState, action) {
    let nextState = state

    const order = orderReducer(state.order, action)
    if (order !== state.order) nextState = nextState.setIn(['order'], order)
    
    const reason = reasonReducer(state.reason, action)
    if (reason !== state.reason) nextState = nextState.setIn(['reason'], reason)

    const icdCode = icdCodeReducer(state.icdCode, action)
    if (icdCode !== state.icdCode) nextState = nextState.setIn(['icdCode'], icdCode)

    const policyHolderRelation = policyHolderRelationReducer(state.policyHolderRelation, action)
    if (policyHolderRelation !== state.policyHolderRelation) nextState = nextState.setIn(['policyHolderRelation'], policyHolderRelation)

    return nextState
}
