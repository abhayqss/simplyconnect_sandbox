import InitialState from './AssessmentInitialState'

import canReducer from './can/canAssessmentReducer'
import listReducer from './list/assessmentListReducer'
import formReducer from './form/assessmentFormReducer'
import countReducer from './count/assessmentCountReducer'
import detailsReducer from './details/assessmentDetailsReducer'
import historyReducer from './history/assessmentHistoryReducer'
import defaultReducer from './default/assessmentDefaultDataReducer'
import anyInProcessReducer from './anyInProcess/isAnyAssessmentInProcessReducer'

import reportReducer from './report/reportReducer'
import servicePlanReducer from './servicePlan/assessmentServicePlanReducer'

const initialState = InitialState()

export default function assessmentReducer(state = initialState, action) {
    let nextState = state

    const can = canReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)

    const anyInProcess = anyInProcessReducer(state.anyInProcess, action)
    if (anyInProcess !== state.anyInProcess) nextState = nextState.setIn(['anyInProcess'], anyInProcess)

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const count = countReducer(state.count, action)
    if (count !== state.count) nextState = nextState.setIn(['count'], count)

    const _default = defaultReducer(state.default, action)
    if (_default !== state.default) nextState = nextState.setIn(['default'], _default)
    
    const form = formReducer(state.form, action)
    if (form !== state.form) nextState = nextState.setIn(['form'], form)

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    const history = historyReducer(state.history, action)
    if (history !== state.history) nextState = nextState.setIn(['history'], history)

    const report = reportReducer(state.report, action)
    if (report !== state.report) nextState = nextState.setIn(['report'], report)
    
    const servicePlan = servicePlanReducer(state.servicePlan, action)
    if (servicePlan !== state.servicePlan) nextState = nextState.setIn(['servicePlan'], servicePlan)

    return nextState
}