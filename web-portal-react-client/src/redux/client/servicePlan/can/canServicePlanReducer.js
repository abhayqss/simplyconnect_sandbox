import CanServicePlanInitialState from './СanServicePlanInitialState'

import addReducer from './add/canAddServicePlanReducer'
import viewReducer from './view/canViewServicePlanReducer'
import reviewByClinicianReducer from './review-by-clinician/canReviewServicePlanByClinicianReducer'

const initialState = new CanServicePlanInitialState()

export default function canServicePlanReducer(state = initialState, action) {
    let nextState = state

    const view = viewReducer(state.view, action)
    if (view !== state.view) nextState = nextState.setIn(['view'], view)

    const add = addReducer(state.add, action)
    if (add !== state.add) nextState = nextState.setIn(['add'], add)

    const reviewByClinician = reviewByClinicianReducer(state.reviewByClinician, action)
    if (reviewByClinician !== state.reviewByClinician) nextState = nextState.setIn(['reviewByClinician'], reviewByClinician)

    return nextState
}
