import { ACTION_TYPES } from 'lib/Constants'

import service from 'services/DirectoryService'

const {
    CLEAR_PRIMARY_FOCUS_LIST,
    LOAD_PRIMARY_FOCUS_LIST_REQUEST,
    LOAD_PRIMARY_FOCUS_LIST_SUCCESS,
    LOAD_PRIMARY_FOCUS_LIST_FAILURE
} = ACTION_TYPES

export function clear () {
    return { type: CLEAR_PRIMARY_FOCUS_LIST }
}

export function load (params, shouldDispatch = true) {
    return dispatch => {
        shouldDispatch && dispatch({
            type: LOAD_PRIMARY_FOCUS_LIST_REQUEST
        })

        return service.findPrimaryFocuses(params).then(response => {
            shouldDispatch && dispatch({
                type: LOAD_PRIMARY_FOCUS_LIST_SUCCESS,
                payload: { data: response.data }
            })

            return response
        }).catch(e => {
            shouldDispatch && dispatch({
                type: LOAD_PRIMARY_FOCUS_LIST_FAILURE, payload: e
            })
        })
    }
}

