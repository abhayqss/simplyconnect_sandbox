import InitialState from './СanAssessmentInitialState'

import addReducer from './add/canAddAssessmentReducer'
import viewReducer from './view/canViewAssessmentsReducer'

const initialState = InitialState()

export default function canAssessmentReducer(state = initialState, action) {
    let nextState = state

    const add = addReducer(state.add, action)
    if (add !== state.add) nextState = nextState.setIn(['add'], add)
    
    const view = viewReducer(state.view, action)
    if (view !== state.view) nextState = nextState.setIn(['view'], view)

    return nextState
}
