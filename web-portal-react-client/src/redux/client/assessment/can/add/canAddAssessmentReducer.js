import { ACTION_TYPES} from 'lib/Constants'

import InitialState from './CanAddAssessmentInitialState'

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA,

    CLEAR_CAN_ADD_ASSESSMENT,
    CLEAR_CAN_ADD_ASSESSMENT_ERROR,
    LOAD_CAN_ADD_ASSESSMENT_REQUEST,
    LOAD_CAN_ADD_ASSESSMENT_SUCCESS,
    LOAD_CAN_ADD_ASSESSMENT_FAILURE
} = ACTION_TYPES

const initialState = new InitialState()

export default function canAddAssessmentReducer(state = initialState, action) {
    if (!(state instanceof InitialState)) {
        return initialState.mergeDeep(state)
    }

    switch (action.type) {
        case LOGOUT_SUCCESS:
        case CLEAR_ALL_AUTH_DATA:
        case CLEAR_CAN_ADD_ASSESSMENT:
            return state.removeIn(['error'])
                .setIn(['isFetching'], false)
                .setIn(['shouldReload'], action.payload || false)
                .removeIn(['value'])

        case CLEAR_CAN_ADD_ASSESSMENT_ERROR:
            return state.removeIn(['error'])

        case LOAD_CAN_ADD_ASSESSMENT_REQUEST: {
            return state.setIn(['error'], null)
                .setIn(['shouldReload'], false)
        }

        case LOAD_CAN_ADD_ASSESSMENT_SUCCESS:
            return state.removeIn(['error'])
                .setIn(['value'], action.payload)

        case LOAD_CAN_ADD_ASSESSMENT_FAILURE:
            return state.setIn(['error'], action.payload)
                .setIn(['shouldReload'], false)
    }

    return state
}