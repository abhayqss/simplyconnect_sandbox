import { ACTION_TYPES } from 'lib/Constants'

import service from 'services/DirectoryService'

const {
    CLEAR_GROUPED_EVENT_TYPE_LIST,
    LOAD_GROUPED_EVENT_TYPE_LIST_SUCCESS,
    LOAD_GROUPED_EVENT_TYPE_LIST_FAILURE
} = ACTION_TYPES

export function clear() {
    return { type: CLEAR_GROUPED_EVENT_TYPE_LIST }
}

export function load(params) {
    return dispatch => {
        return service.findGroupedEventTypes(params).then(response => {
            const { data } = response

            dispatch({
                type: LOAD_GROUPED_EVENT_TYPE_LIST_SUCCESS,
                payload: { data }
            })
        }).catch(e => {
            dispatch({ type: LOAD_GROUPED_EVENT_TYPE_LIST_FAILURE, payload: e })
        })
    }
}