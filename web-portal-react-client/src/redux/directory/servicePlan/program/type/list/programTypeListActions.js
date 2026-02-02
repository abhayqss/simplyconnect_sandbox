import { ACTION_TYPES } from 'lib/Constants'

import service from 'services/DirectoryService'

const {
    CLEAR_PROGRAM_TYPE_LIST,
    LOAD_PROGRAM_TYPE_LIST_REQUEST,
    LOAD_PROGRAM_TYPE_LIST_SUCCESS,
    LOAD_PROGRAM_TYPE_LIST_FAILURE
} = ACTION_TYPES

export function clear () {
    return { type: CLEAR_PROGRAM_TYPE_LIST }
}

export function load () {
    return dispatch => {
        dispatch({ type: LOAD_PROGRAM_TYPE_LIST_REQUEST })

        return service.findProgramTypes().then(response => {
            dispatch({
                type: LOAD_PROGRAM_TYPE_LIST_SUCCESS,
                payload: response.data
            })
        }).catch(e => {
            dispatch({
                type: LOAD_PROGRAM_TYPE_LIST_FAILURE,
                payload: e
            })
        })
    }
}

