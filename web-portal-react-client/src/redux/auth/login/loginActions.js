import authService from 'services/AuthService'

import { ACTION_TYPES } from 'lib/Constants'
import authUserStore from 'lib/stores/AuthUserStore'

import { fireAbortAllEvent } from 'lib/utils/AjaxUtils'

const {
    LOGIN_REQUEST,
    LOGIN_SUCCESS,
    LOGIN_FAILURE,
    LOGIN_4D_REQUEST,
    LOGIN_4D_SUCCESS,
    LOGIN_4D_FAILURE,
    CLEAR_LOGIN_ERROR,
    CLEAR_ALL_AUTH_DATA,
    RESTORE_LOGGED_IN_USER,
} = ACTION_TYPES

export function clearError () {
    return { type: CLEAR_LOGIN_ERROR }
}

export function login (data, params) {
    fireAbortAllEvent()

    return dispatch => {
        dispatch({ type: LOGIN_REQUEST })
        return authService
            .login(data, params)
            .then(response => {
                dispatch({ type: LOGIN_SUCCESS, payload: response.data })

                authUserStore.save(response.data)

                return response
            })
            .catch(e => {
                dispatch({ type: LOGIN_FAILURE, payload: e })
                throw e
            })
    }
}

export function loginFrom4d(data) {
    return dispatch => {
        dispatch({ type: LOGIN_4D_REQUEST })

        return authService
            .loginFrom4d(data)
            .then(response => {
                dispatch({ type: LOGIN_4D_SUCCESS, payload: response.data.user })

                authUserStore.save(response.data.user)

                return {
                    ...response,
                    ...data
                }
            })
            .catch(e => {
                dispatch({ type: LOGIN_4D_FAILURE, payload: e })
            })
    }
}

export function restore () {
    return { type: RESTORE_LOGGED_IN_USER, payload: authUserStore.get() }
}

export function remove () {
    authUserStore.clear()
    return { type: CLEAR_ALL_AUTH_DATA }
}