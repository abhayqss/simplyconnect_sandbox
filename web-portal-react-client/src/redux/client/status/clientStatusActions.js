import service from 'services/ClientService'

import { ACTION_TYPES } from 'lib/Constants'

const {
    LOAD_CLIENT_STATUS_REQUEST,
    LOAD_CLIENT_STATUS_SUCCESS,
    LOAD_CLIENT_STATUS_FAILURE,
} = ACTION_TYPES

export function toggle(clientId) {
    return dispatch => {
        dispatch({ type: LOAD_CLIENT_STATUS_REQUEST })

        return service.toggleStatus(clientId).then(response => {
            dispatch({ type: LOAD_CLIENT_STATUS_SUCCESS, payload: response.data })

            return response
        }).catch(e => {
            dispatch({ type: LOAD_CLIENT_STATUS_FAILURE, payload: e })
        })
    }
}
