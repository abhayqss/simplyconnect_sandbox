import authService from 'services/AuthService'

import { ACTION_TYPES } from 'lib/Constants'

const {
    CLEAR_SESSION_ERROR,
    SET_SESSION_ABOUT_TO_EXPIRE
} = ACTION_TYPES

export function validate () {
    return () => {
        return authService.validateSession()
    }
}

export function pulse() {
    return () => {
        return authService.pulseSession()
    }
}

export function clearError () {
    return { type: CLEAR_SESSION_ERROR }
}

export function setAboutToExpire(isAboutToExpire) {
    return { type: SET_SESSION_ABOUT_TO_EXPIRE, payload: isAboutToExpire }
}

export default {
    pulse,
    validate,
    clearError
}