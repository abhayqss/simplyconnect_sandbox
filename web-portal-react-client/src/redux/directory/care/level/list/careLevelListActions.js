import { ACTION_TYPES } from 'lib/Constants'

import service from 'services/DirectoryService'

const {
    CLEAR_CARE_LEVEL_LIST,
    LOAD_CARE_LEVEL_LIST_SUCCESS,
    LOAD_CARE_LEVEL_LIST_FAILURE
} = ACTION_TYPES

export function clear () {
    return { type: CLEAR_CARE_LEVEL_LIST }
}

export function load (params) {
    return dispatch => {
        return service.findCareLevels(params).then(response => {
            const { data } = response

            dispatch({
                type: LOAD_CARE_LEVEL_LIST_SUCCESS,
                payload: { data }
            })
        }).catch(e => {
            dispatch({ type: LOAD_CARE_LEVEL_LIST_FAILURE, payload: e })
        })
    }
}

