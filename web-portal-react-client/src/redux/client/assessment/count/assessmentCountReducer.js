import { ACTION_TYPES} from 'lib/Constants'

import InitialState from './AssessmentCountInitialState'

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA,

    CLEAR_ASSESSMENT_COUNT,
    CLEAR_ASSESSMENT_COUNT_ERROR,
    LOAD_ASSESSMENT_COUNT_REQUEST,
    LOAD_ASSESSMENT_COUNT_SUCCESS,
    LOAD_ASSESSMENT_COUNT_FAILURE
} = ACTION_TYPES

const initialState = new InitialState()

export default function assessmentCountReducer (state = initialState, action) {
    if (!(state instanceof InitialState)) {
        return initialState.mergeDeep(state)
    }

    switch (action.type) {
        case LOGOUT_SUCCESS:
        case CLEAR_ALL_AUTH_DATA:
        case CLEAR_ASSESSMENT_COUNT:
            return state.removeIn(['error'])
                .removeIn(['value'])
                .removeIn(['isFetching'])
                .removeIn(['fetchCount'])
                .setIn(['shouldReload'], action.payload || false)

        case CLEAR_ASSESSMENT_COUNT_ERROR:
            return state.removeIn(['error'])

        case LOAD_ASSESSMENT_COUNT_REQUEST: {
            return state.setIn(['error'], null)
                .setIn(['isFetching'], true)
                .setIn(['shouldReload'], false)
        }

        case LOAD_ASSESSMENT_COUNT_SUCCESS:
            return state.removeIn(['error'])
                .setIn(['isFetching'], false)
                .setIn(['value'], action.payload)
                .setIn(['fetchCount'], state.fetchCount + 1)

        case LOAD_ASSESSMENT_COUNT_FAILURE:
            return state.setIn(['error'], action.payload)
                .setIn(['isFetching'], false)
                .setIn(['shouldReload'], false)
                .setIn(['fetchCount'], state.fetchCount + 1)
    }

    return state
}