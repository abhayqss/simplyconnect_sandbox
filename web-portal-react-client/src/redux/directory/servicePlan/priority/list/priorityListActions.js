import { ACTION_TYPES } from 'lib/Constants'

import service from 'services/DirectoryService'

const {
    CLEAR_PRIORITY_LIST,
    LOAD_PRIORITY_LIST_REQUEST,
    LOAD_PRIORITY_LIST_SUCCESS,
    LOAD_PRIORITY_LIST_FAILURE
} = ACTION_TYPES

export function clear() {
    return { type: CLEAR_PRIORITY_LIST }
}

export function load(params) {
    return dispatch => {
        dispatch({ type: LOAD_PRIORITY_LIST_REQUEST })

        return service.findPriorities(params).then(response => {
            dispatch({
                type: LOAD_PRIORITY_LIST_SUCCESS,
                payload: response.data
            })
        }).catch(e => {
            dispatch({ type: LOAD_PRIORITY_LIST_FAILURE, payload: e })
        })
    }
}

