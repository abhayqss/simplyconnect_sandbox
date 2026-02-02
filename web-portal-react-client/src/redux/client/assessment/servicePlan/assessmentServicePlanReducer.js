import InitialState from './AssessmentServicePlanInitialState'

import needIdentificationReducer from './needIdentification/assessmentServicePlanNeedIdentificationReducer'

const initialState = InitialState()

export default function assessmentServicePlanReducer(state = initialState, action) {
    let nextState = state

    const needIdentification = needIdentificationReducer(state.needIdentification, action)
    if (needIdentification !== state.needIdentification) nextState = nextState.setIn(['needIdentification'], needIdentification)

    return nextState
}