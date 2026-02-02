import InitialState from './ClientDashboardInitialState'

import noteReducer from './note/clientNoteReducer'
import eventReducer from './event/clientEventReducer'
import assessmentReducer from './assessment/clientAssessmentReducer'
import clientServicePlanReducer from './servicePlan/clientServicePlanReducer'

const initialState = new InitialState()

export default function clientDashboardReducer(state = initialState, action) {
    let nextState = state

    const note = noteReducer(state.note, action)
    if (note !== state.note) nextState = nextState.setIn(['note'], note)

    const event = eventReducer(state.event, action)
    if (event !== state.event) nextState = nextState.setIn(['event'], event)

    const assessment = assessmentReducer(state.assessment, action)
    if (assessment !== state.assessment) nextState = nextState.setIn(['assessment'], assessment)
    
    const servicePlan = clientServicePlanReducer(state.servicePlan, action)
    if (servicePlan !== state.servicePlan) nextState = nextState.setIn(['servicePlan'], servicePlan)

    return nextState
}