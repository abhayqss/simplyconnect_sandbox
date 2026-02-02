import { ACTION_TYPES } from 'lib/Constants'

import service from 'services/ClientService'

const {
    CLEAR_CAN_EDIT_CLIENT,
    CLEAR_CAN_EDIT_CLIENT_ERROR,
    LOAD_CAN_EDIT_CLIENT_REQUEST,
    LOAD_CAN_EDIT_CLIENT_SUCCESS,
    LOAD_CAN_EDIT_CLIENT_FAILURE,
} = ACTION_TYPES

export function clear() {
    return { type: CLEAR_CAN_EDIT_CLIENT }
}

export function clearError() {
    return { type: CLEAR_CAN_EDIT_CLIENT_ERROR }
}

export function load(params) {
    return dispatch => {
        dispatch({ type: LOAD_CAN_EDIT_CLIENT_REQUEST })

        return service.canEdit(params).then(response => {
            dispatch({ type: LOAD_CAN_EDIT_CLIENT_SUCCESS, payload: response.data })

            return response
        }).catch(e => {
            dispatch({ type: LOAD_CAN_EDIT_CLIENT_FAILURE, payload: e })
        })
    }
}
