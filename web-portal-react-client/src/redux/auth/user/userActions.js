import authService from 'services/AuthService'

import { ACTION_TYPES } from 'lib/Constants'
import authUserStore from 'lib/stores/AuthUserStore'

const {
    RESTORE_LOGGED_IN_USER,
    UPDATE_AUTH_USER_REQUEST,
    UPDATE_AUTH_USER_SUCCESS,
    UPDATE_AUTH_USER_FAILURE,
    CLEAR_UPDATE_AUTH_USER_ERROR
} = ACTION_TYPES

export function clearError () {
    return { type: CLEAR_UPDATE_AUTH_USER_ERROR }
}

export function updateAuthUser() {
    return dispatch => {
        dispatch({ type: UPDATE_AUTH_USER_REQUEST })

        authService
            .getAuthUser()
            .then(response => {
                dispatch({ type: UPDATE_AUTH_USER_SUCCESS })

                authUserStore.save(response.data)

                dispatch({ type: RESTORE_LOGGED_IN_USER, payload: response.data })

                return response
            })
            .catch(e => {
                dispatch({ type: UPDATE_AUTH_USER_FAILURE, payload: e })
            })
    }
}