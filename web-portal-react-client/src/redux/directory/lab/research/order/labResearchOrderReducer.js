import InitialState from './LabResearchOrderInitialState'

import statusReducer from './status/labResearchOrderStatusReducer'
import specimenTypeReducer from './specimen-type/labResearchSpecimenTypeReducer'

const initialState = new InitialState()

export default function labResearchOrderReducer(state = initialState, action) {
    let nextState = state

    const status = statusReducer(state.status, action)
    if (status !== state.status) nextState = nextState.setIn(['status'], status)

    const specimenType = specimenTypeReducer(state.specimenType, action)
    if (specimenType !== state.specimenType) nextState = nextState.setIn(['specimenType'], specimenType)

    return nextState
}
