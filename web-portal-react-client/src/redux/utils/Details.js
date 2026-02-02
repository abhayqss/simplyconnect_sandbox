import { ACTION_TYPES } from 'lib/Constants'

import BaseState from './base/State'
import BaseActions from './base/Actions'

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA
} = ACTION_TYPES

export function ActionTypes(entity) {
    return {
        CLEAR: `CLEAR_${entity}_DETAILS`,
        REFRESH: `REFRESH_${entity}_DETAILS`,
        CLEAR_ERROR: `CLEAR_${entity}_DETAILS_ERROR`,
        LOAD_REQUEST: `LOAD_${entity}_DETAILS_REQUEST`,
        LOAD_SUCCESS: `LOAD_${entity}_DETAILS_SUCCESS`,
        LOAD_FAILURE: `LOAD_${entity}_DETAILS_FAILURE`,
        DOWNLOAD_FAILURE: `DOWNLOAD_${entity}_DETAILS_FAILURE`
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

export function Actions({ actionTypes, doLoad, doDownload }) {
    return BaseActions({ actionTypes, doLoad, doDownload })
}

export function Reducer({ stateClass, actionTypes = {}, extReducer }) {
    const {
        CLEAR,
        REFRESH,
        CLEAR_ERROR,
        LOAD_REQUEST,
        LOAD_SUCCESS,
        LOAD_FAILURE,
        DOWNLOAD_FAILURE
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

            case REFRESH: 
                return state.setShouldReload(true)

            case LOAD_REQUEST: {
                return state
                    .clearError()
                    .setFetching(true)
                    .setShouldReload(false)
            }

            case LOAD_SUCCESS:
                return state
                    .incFetchCount()
                    .setFetching(false)
                    .setShouldReload(false)
                    .setData(action.payload.data)

            case LOAD_FAILURE:
            case DOWNLOAD_FAILURE:
                return state
                    .incFetchCount()
                    .setFetching(false)
                    .setShouldReload(false)
                    .setError(action.payload)
        }

        return extReducer ? extReducer(state, action) : state
    }
}