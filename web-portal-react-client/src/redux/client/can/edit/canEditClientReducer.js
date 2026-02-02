import { ACTION_TYPES} from 'lib/Constants'

import InitialState from './CanEditClientInitialState'

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA,

    CLEAR_CAN_EDIT_CLIENT,
    CLEAR_CAN_EDIT_CLIENT_ERROR,
    LOAD_CAN_EDIT_CLIENT_REQUEST,
    LOAD_CAN_EDIT_CLIENT_SUCCESS,
    LOAD_CAN_EDIT_CLIENT_FAILURE
} = ACTION_TYPES

const initialState = new InitialState()

export default function canAddServicePlanReducer(state = initialState, action) {
    if (!(state instanceof InitialState)) {
        return initialState.mergeDeep(state)
    }

    switch (action.type) {
        case LOGOUT_SUCCESS:
        case CLEAR_ALL_AUTH_DATA:
        case CLEAR_CAN_EDIT_CLIENT:
            return state.removeIn(['error'])
                .setIn(['isFetching'], false)
                .setIn(['shouldReload'], action.payload || false)
                .removeIn(['value'])

        case CLEAR_CAN_EDIT_CLIENT_ERROR:
            return state.removeIn(['error'])

        case LOAD_CAN_EDIT_CLIENT_REQUEST: {
            return state.setIn(['error'], null)
                .setIn(['shouldReload'], false)
        }

        case LOAD_CAN_EDIT_CLIENT_SUCCESS:
            return state.removeIn(['error'])
                .setIn(['value'], action.payload)

        case LOAD_CAN_EDIT_CLIENT_FAILURE:
            return state.setIn(['error'], action.payload)
                .setIn(['shouldReload'], false)
    }

    return state
}