import CanLabResearchOrderInitialState from './CanLabResearchOrderInitialState'

import addReducer from './add/canAddLabResearchOrderReducer'
import reviewReducer from './review/canReviewLabResearchOrderReducer'

const initialState = new CanLabResearchOrderInitialState()

export default function canReferralRequestReducer(state = initialState, action) {
    let nextState = state

    const add = addReducer(state.add, action)
    if (add !== state.add) nextState = nextState.setIn(['add'], add)

    const review = reviewReducer(state.review, action)
    if (review !== state.review) nextState = nextState.setIn(['review'], review)

    return nextState
}