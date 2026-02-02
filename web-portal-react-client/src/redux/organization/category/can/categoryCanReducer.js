import InitialState from './CategoryCanInitialState'

import addReducer from './add/canAddCategoriesReducer'
import viewReducer from './view/canViewCategoriesReducer'

const initialState = new InitialState()

export default function categoryCanReducer(state = initialState, action) {
    let nextState = state

    const add = addReducer(state.add, action)
    if (add !== state.add) nextState = nextState.setIn(['add'], add)

    const view = viewReducer(state.view, action)
    if (view !== state.view) nextState = nextState.setIn(['view'], view)

    return nextState
}