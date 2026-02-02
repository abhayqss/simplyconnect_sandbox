import { ACTION_TYPES } from 'lib/Constants'

import InitialState from './ClientLastViewedInitialState'
import actionTypes from './clientLastViewedActionTypes'

const { SET, CLEAR } = actionTypes

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA
} = ACTION_TYPES

export default function(state = InitialState(), action) {
    switch (action.type) {
        case CLEAR:
        case LOGOUT_SUCCESS:
        case CLEAR_ALL_AUTH_DATA:
            state = state.clear()
            break

        case SET:
            state = state.set('id', action.payload)
            break
    }

    return state
}