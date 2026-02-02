import actionTypes from './clientRouteActionTypes'
import InitialState from './ClientRouteInitialState'

import { ACTION_TYPES } from 'lib/Constants'

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA
} = ACTION_TYPES

const {
    CLEAR,
    CHANGE,
} = actionTypes

export default function(state = InitialState(), action) {
    switch (action.type) {
        case CLEAR:
        case LOGOUT_SUCCESS:
        case CLEAR_ALL_AUTH_DATA:
            state = state.clear()
            break

        case CHANGE:
            state = state.set('value', action.payload)
            break
    }

    return state
}
