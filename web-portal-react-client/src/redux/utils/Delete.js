import { ACTION_TYPES } from 'lib/Constants'

import BaseState from './base/State'

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA
} = ACTION_TYPES

export function ActionTypes(entity) {
    return {
        CLEAR: `CLEAR_DELETE_${entity}`,
        CLEAR_ERROR: `CLEAR_DELETE_${entity}_ERROR`,
        DELETE_REQUEST: `DELETE_${entity}_REQUEST`,
        DELETE_SUCCESS: `DELETE_${entity}_SUCCESS`,
        DELETE_FAILURE: `DELETE_${entity}_FAILURE`
    }
}

export function State() {
    return BaseState({
        data: null,
        setData(data) {
            return this.setIn(['data'], data)
        }
    })
}

export function Actions({ actionTypes, doDelete }) {
    const {
        DELETE_REQUEST,
        DELETE_SUCCESS,
        DELETE_FAILURE,
    } = actionTypes

    return {
        clear: () => ({ type: actionTypes.CLEAR }),
        clearError: () => ({ type: actionTypes.CLEAR_ERROR }),
        delete: (...args) => {
            return dispatch => {
                dispatch({ type: DELETE_REQUEST })
                return doDelete(...args).then(response => {
                    dispatch({ type: DELETE_SUCCESS, payload: { data: response.data } })
                    return response
                }).catch(error => {
                    dispatch({ type: DELETE_FAILURE, payload: error })
                    return { error }
                })
            }
        }
    }
}

export function Reducer({ stateClass, actionTypes = {}, extReducer }) {
    const {
        CLEAR,
        CLEAR_ERROR,
        DELETE_REQUEST,
        DELETE_SUCCESS,
        DELETE_FAILURE
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

            case DELETE_REQUEST: {
                return state
                    .clearError()
                    .setFetching(true)
            }

            case DELETE_SUCCESS:
                return state
                    .incFetchCount()
                    .setFetching(false)
                    .setData(action.payload.data)

            case DELETE_FAILURE:
                return state
                    .incFetchCount()
                    .setFetching(false)
                    .setError(action.payload)
        }

        return extReducer ? extReducer(state, action) : state
    }
}