import InitialState from './ProgramSubTypeListInitialState'

import { ACTION_TYPES } from 'lib/Constants'

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA,

    CLEAR_PROGRAM_SUBTYPE_LIST,
    LOAD_PROGRAM_SUBTYPE_LIST_REQUEST,
    LOAD_PROGRAM_SUBTYPE_LIST_SUCCESS,
    LOAD_PROGRAM_SUBTYPE_LIST_FAILURE
} = ACTION_TYPES

const initialState = new InitialState()

export default function programSubTypeListReducer(state = initialState, action) {
    if (!(state instanceof InitialState)) {
        return initialState.mergeDeep(state)
    }

    switch (action.type) {
        case LOGOUT_SUCCESS:
        case CLEAR_ALL_AUTH_DATA:
        case CLEAR_PROGRAM_SUBTYPE_LIST:
            return state.clear()

        case LOAD_PROGRAM_SUBTYPE_LIST_REQUEST:
            return state
                .removeIn(['error'])
                .setIn(['isFetching'], true)

        case LOAD_PROGRAM_SUBTYPE_LIST_SUCCESS:
            return state
                .setIn(['isFetching'], false)
                .setIn(['dataSource', 'data'], action.payload)

        case LOAD_PROGRAM_SUBTYPE_LIST_FAILURE:
            return state
                .setIn(['error'], action.payload)
    }

    return state
}
