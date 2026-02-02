import { ACTION_TYPES } from 'lib/Constants'

import BaseState from './base/State'
import BaseActions from './base/Actions'

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA
} = ACTION_TYPES

export function ActionTypes(entity) {
    return {
        CLEAR: `CLEAR_${entity}`,
        CLEAR_ERROR: `CLEAR_${entity}_ERROR`,
        LOAD_REQUEST: `LOAD_${entity}_REQUEST`,
        LOAD_SUCCESS: `LOAD_${entity}_SUCCESS`,
        LOAD_FAILURE: `LOAD_${entity}_FAILURE`
    }
}

export function State() {
    return BaseState({
        value: null,
        setValue(value) {
            return this.setIn(['value'], value)
        }
    })
}

export function Actions({ actionTypes, doLoad }) {
    return BaseActions({ actionTypes, doLoad })
}

export function Reducer({ stateClass, actionTypes = {}, extReducer }) {
    const {
        CLEAR,
        CLEAR_ERROR,
        LOAD_REQUEST,
        LOAD_SUCCESS,
        LOAD_FAILURE
    } = actionTypes

    const initialState = new stateClass()

    return function reducer(state = initialState, action) {
        if (!(state instanceof stateClass)) {
            return initialState.mergeDeep(state)
        }

        switch (action.type) {
            case CLEAR:
            case LOGOUT_SUCCESS:
            case CLEAR_ALL_AUTH_DATA:
                return state.clear()

            case CLEAR_ERROR:
                return state.setError(null)

            case LOAD_REQUEST: {
                return state
                    .clearError()
                    .setValue(null)
                    .setFetching(true)
                    .setShouldReload(false)
            }

            case LOAD_SUCCESS:
                return state
                    .incFetchCount()
                    .setFetching(false)
                    .setShouldReload(false)
                    .setValue(action.payload.data)

            case LOAD_FAILURE:
                return state
                    .incFetchCount()
                    .setFetching(false)
                    .setShouldReload(false)
                    .setError(action.payload)
        }

        return extReducer ? extReducer(state, action) : state
    }
}