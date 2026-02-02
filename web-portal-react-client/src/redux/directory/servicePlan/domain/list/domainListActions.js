import { ACTION_TYPES } from 'lib/Constants'

import service from 'services/DirectoryService'

const {
    CLEAR_DOMAIN_LIST,
    LOAD_DOMAIN_LIST_REQUEST,
    LOAD_DOMAIN_LIST_SUCCESS,
    LOAD_DOMAIN_LIST_FAILURE
} = ACTION_TYPES

export function clear() {
    return { type: CLEAR_DOMAIN_LIST }
}

export function load(params) {
    return dispatch => {
        dispatch({ type: LOAD_DOMAIN_LIST_REQUEST })

        return service.findDomains(params).then(response => {
            dispatch({
                type: LOAD_DOMAIN_LIST_SUCCESS,
                payload: response.data
            })
        }).catch(e => {
            dispatch({ type: LOAD_DOMAIN_LIST_FAILURE, payload: e })
        })
    }
}

