import InitialState from './ClientMedicationInitialState'

import countReducer from './count/clientMedicationCountReducer'

const initialState = InitialState()

export default function clientMedicationReducer(state = initialState, action) {
    let nextState = state

    const count = countReducer(state.count, action)
    if (count !== state.count) nextState = nextState.setIn(['count'], count)

    return nextState
}