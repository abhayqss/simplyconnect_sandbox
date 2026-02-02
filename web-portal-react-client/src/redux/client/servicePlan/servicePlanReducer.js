import ServicePlanInitialState from './ServicePlanInitialState'

import canReducer from './can/canServicePlanReducer'
import formReducer from './form/servicePlanFormReducer'
import listReducer from './list/servicePlanListReducer'
import countReducer from './count/servicePlanCountReducer'
import controlledReducer from './controlled/controlledReducer'
import detailsReducer from './details/servicePlanDetailsReducer'
import historyReducer from './history/servicePlanHistoryReducer'
import resourceNameReducer from './resource-name/resourceNameReducer'

const initialState = new ServicePlanInitialState()

export default function servicePlanReducer(state = initialState, action) {
    let nextState = state

    const can = canReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)

    const form = formReducer(state.form, action)
    if (form !== state.form) nextState = nextState.setIn(['form'], form)

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const count = countReducer(state.count, action)
    if (count !== state.count) nextState = nextState.setIn(['count'], count)

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    const history = historyReducer(state.history, action)
    if (history !== state.history) nextState = nextState.setIn(['history'], history)

    const controlled = controlledReducer(state.controlled, action)
    if (controlled !== state.controlled) nextState = nextState.setIn(['controlled'], controlled)

    const resourceName = resourceNameReducer(state.resourceName, action)
    if (resourceName !== state.resourceName) nextState = nextState.setIn(['resourceName'], resourceName)

    return nextState
}