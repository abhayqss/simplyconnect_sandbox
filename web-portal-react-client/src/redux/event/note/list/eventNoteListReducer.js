import { isNumber } from 'underscore'

import { ACTION_TYPES } from 'lib/Constants'

import InitialState from './EventNoteListInitialState'

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA,

    CLEAR_EVENT_NOTE_LIST_ERROR,

    CLEAR_EVENT_NOTE_LIST,
    CLEAR_EVENT_NOTE_LIST_FILTER,
    CHANGE_EVENT_NOTE_LIST_FILTER,
    CHANGE_EVENT_NOTE_LIST_FILTER_FIELD,

    LOAD_EVENT_NOTE_LIST_REQUEST,
    LOAD_EVENT_NOTE_LIST_SUCCESS,
    LOAD_EVENT_NOTE_LIST_FAILURE,

    SAVE_NOTE_SUCCESS,
    SAVE_EVENT_SUCCESS,
    SAVE_GROUP_NOTE_SUCCESS
} = ACTION_TYPES

const initialState = new InitialState()

export default function eventNoteListReducer(state = initialState, action) {
    if (!(state instanceof InitialState)) {
        return initialState.mergeDeep(state)
    }

    switch (action.type) {
        case LOGOUT_SUCCESS:
        case CLEAR_ALL_AUTH_DATA:
        case CLEAR_EVENT_NOTE_LIST:
            return state.clear()

        case CLEAR_EVENT_NOTE_LIST_ERROR:
            return state.clearError()

        case CLEAR_EVENT_NOTE_LIST_FILTER:
            return state.clearFilter(action.payload.defaults)
                .setShouldReload(action.payload.shouldReload ?? true)

        case CHANGE_EVENT_NOTE_LIST_FILTER:
            return state.changeFilter(action.payload.changes)
                .setShouldReload(action.payload.shouldReload ?? true)

        case CHANGE_EVENT_NOTE_LIST_FILTER_FIELD: {
            const {
                name, value, shouldReload = true
            } = action.payload

            return state.changeFilterField(name, value)
                .setShouldReload(shouldReload)
        }

        case SAVE_NOTE_SUCCESS:
        case SAVE_EVENT_SUCCESS:
        case SAVE_GROUP_NOTE_SUCCESS:
            return state
                .clearError()
                .setShouldReload(true)

        case LOAD_EVENT_NOTE_LIST_REQUEST:
            return state
                .clearError()
                .setFetching(true)
                .setShouldReload(false)

        case LOAD_EVENT_NOTE_LIST_SUCCESS: {
            const {
                data,
                page,
                size,
                totalCount
            } = action.payload

            return state
                .setData(data)
                .setFetching(false)
                .setPagination(isNumber(page) ? {
                    page, size, totalCount
                } : {})
        }

        case LOAD_EVENT_NOTE_LIST_FAILURE:
            return state
                .setFetching(false)
                .setError(action.payload)
    }

    return state
}