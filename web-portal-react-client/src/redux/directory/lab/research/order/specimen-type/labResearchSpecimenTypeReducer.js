import InitialState from './LabResearchSpecimenTypeInitialState'

import listReducer from './list/labResearchSpecimenTypeListReducer'

const initialState = new InitialState()

export default function labResearchSpecimenTypeReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}
