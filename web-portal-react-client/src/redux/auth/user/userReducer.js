import { ACTION_TYPES } from 'lib/Constants'
import UserInitialState from './UserInitialState'

const {
    UPDATE_AUTH_USER_REQUEST,
    UPDATE_AUTH_USER_SUCCESS,
    UPDATE_AUTH_USER_FAILURE,
    CLEAR_UPDATE_AUTH_USER_ERROR
} = ACTION_TYPES

const initialState = new UserInitialState()

export default function userReducer(state = initialState, action) {
    if (!(state instanceof UserInitialState)) {
        return initialState.mergeDeep(state)
    }

    switch (action.type) {
        case CLEAR_UPDATE_AUTH_USER_ERROR:
            return state.removeIn(['error'])

        case UPDATE_AUTH_USER_REQUEST:
            return state.setIn(['isFetching'], true)

        case UPDATE_AUTH_USER_SUCCESS:
            return state
                .setIn(['isFetching'], false)

        case UPDATE_AUTH_USER_FAILURE:
            return state
                .setIn(['error'], action.payload)
                .setIn(['isFetching'], false)
    }

    return state
}
