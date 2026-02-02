import { ACTION_TYPES } from 'lib/Constants'

import service from 'services/DirectoryService'

const {
    CLEAR_COMMUNITY_TYPE_LIST,
    LOAD_COMMUNITY_TYPE_LIST_REQUEST,
    LOAD_COMMUNITY_TYPE_LIST_SUCCESS,
    LOAD_COMMUNITY_TYPE_LIST_FAILURE
} = ACTION_TYPES

export function clear () {
    return { type: CLEAR_COMMUNITY_TYPE_LIST }
}

export function load (params) {
    return dispatch => {
        dispatch({ type: LOAD_COMMUNITY_TYPE_LIST_REQUEST })
        return service.findCommunityTypes(params).then(response => {
            dispatch({
                type: LOAD_COMMUNITY_TYPE_LIST_SUCCESS,
                payload: { data: response.data }
            })

            return response
        }).catch(e => {
            dispatch({ type: LOAD_COMMUNITY_TYPE_LIST_FAILURE, payload: e })
        })
    }
}

