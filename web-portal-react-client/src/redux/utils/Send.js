import { ACTION_TYPES } from 'lib/Constants'

import BaseState from './base/State'
import BaseActions from './base/Actions'
import { isFunction } from 'underscore'

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA
} = ACTION_TYPES

export function ActionTypes(entity) {
    return {
        CLEAR: `CLEAR_${entity}`,
        CLEAR_ERROR: `CLEAR_${entity}_ERROR`,
        SEND_REQUEST: `SEND_${entity}_REQUEST`,
        SEND_SUCCESS: `SEND_${entity}_SUCCESS`,
        SEND_FAILURE: `SEND_${entity}_FAILURE`
    }
}

export function State() {
    return BaseState({
        result: null,
        setResult(result) {
            return this.setIn(['result'], result)
        }
    })
}

export function Actions({ actionTypes, doSend }) {
    const {
        SEND_REQUEST,
        SEND_SUCCESS,
        SEND_FAILURE,
    } = actionTypes

    return {
        ...BaseActions({ actionTypes }),
        ...isFunction(doSend) && {
            send: (...args) => {
                return dispatch => {
                    dispatch({ type: SEND_REQUEST })
                    return doSend(...args).then(response => {
                        dispatch({ type: SEND_SUCCESS, payload: response.data })
                        return response
                    }).catch(error => {
                        dispatch({ type: SEND_FAILURE, payload: error })
                        return { error }
                    })
                }
            }
        }
    }
}

export function Reducer({ stateClass, actionTypes = {}, extReducer }) {
    const {
        CLEAR,
        CLEAR_ERROR,
        SEND_REQUEST,
        SEND_SUCCESS,
        SEND_FAILURE
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

            case SEND_REQUEST: {
                return state
                    .clearError()
                    .setFetching(true)
                    .setShouldReload(false)
            }

            case SEND_SUCCESS:
                return state
                    .incFetchCount()
                    .setFetching(false)
                    .setShouldReload(false)
                    .setResult(action.payload)

            case SEND_FAILURE:
                return state
                    .incFetchCount()
                    .setFetching(false)
                    .setShouldReload(false)
                    .setError(action.payload)
        }

        return extReducer ? extReducer(state, action) : state
    }
}