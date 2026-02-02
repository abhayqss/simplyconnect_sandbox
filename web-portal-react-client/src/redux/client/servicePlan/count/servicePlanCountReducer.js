import { ACTION_TYPES} from 'lib/Constants'

import InitialState from './ServicePlanCountInitialState'

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA,

    CLEAR_SERVICE_PLAN_COUNT,
    CLEAR_SERVICE_PLAN_COUNT_ERROR,
    LOAD_SERVICE_PLAN_COUNT_REQUEST,
    LOAD_SERVICE_PLAN_COUNT_SUCCESS,
    LOAD_SERVICE_PLAN_COUNT_FAILURE
} = ACTION_TYPES

const initialState = new InitialState()

export default function contactCountReducer (state = initialState, action) {
    if (!(state instanceof InitialState)) {
        return initialState.mergeDeep(state)
    }

    switch (action.type) {
        case LOGOUT_SUCCESS:
        case CLEAR_ALL_AUTH_DATA:
        case CLEAR_SERVICE_PLAN_COUNT:
            return state.removeIn(['error'])
                .removeIn(['value'])
                .removeIn(['isFetching'])
                .removeIn(['fetchCount'])
                .setIn(['shouldReload'], action.payload || false)

        case CLEAR_SERVICE_PLAN_COUNT_ERROR:
            return state.removeIn(['error'])

        case LOAD_SERVICE_PLAN_COUNT_REQUEST: {
            return state.setIn(['error'], null)
                .setIn(['isFetching'], true)
                .setIn(['shouldReload'], false)
        }

        case LOAD_SERVICE_PLAN_COUNT_SUCCESS:
            return state.removeIn(['error'])
                .setIn(['isFetching'], false)
                .setIn(['value'], action.payload)
                .setIn(['fetchCount'], state.fetchCount + 1)

        case LOAD_SERVICE_PLAN_COUNT_FAILURE:
            return state.setIn(['error'], action.payload)
                .setIn(['isFetching'], false)
                .setIn(['shouldReload'], false)
                .setIn(['fetchCount'], state.fetchCount + 1)
    }

    return state
}