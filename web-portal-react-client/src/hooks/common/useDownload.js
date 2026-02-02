import { useMemo, useReducer, useCallback } from 'react'

import { saveAs } from 'file-saver'
import useRefCurrent from "./useRefCurrent";

const { Record } = require('immutable')

function ActionTypes(entity) {
    return {
        CLEAR_ERROR: `CLEAR_DOWNLOAD_${entity}_ERROR`,
        DOWNLOAD_REQUEST: `DOWNLOAD_${entity}_REQUEST`,
        DOWNLOAD_SUCCESS: `DOWNLOAD_${entity}_SUCCESS`,
        DOWNLOAD_FAILURE: `DOWNLOAD_${entity}_FAILURE`,
    }
}

function Actions({ actionTypes, doDownload }) {
    const {
        CLEAR_ERROR,
        DOWNLOAD_REQUEST,
        DOWNLOAD_SUCCESS,
        DOWNLOAD_FAILURE
    } = actionTypes

    return {
        clearError: () => ({ type: CLEAR_ERROR }),
        download: (...args) => {
            return dispatch => {
                dispatch({ type: DOWNLOAD_REQUEST })
                return (
                    doDownload(...args).then(({ name, data }) => {
                        dispatch({ type: DOWNLOAD_SUCCESS, payload: { data } })
                        saveAs(data, name)
                        return { success: true, name }
                    }).catch(error => {
                        dispatch({ type: DOWNLOAD_FAILURE, payload: error })
                        return { error }
                    })
                )
            }
        }
    }
}

const State = Record({
    data: null,
    setData(data) {
        return this.set('data', data)
    },
    error: null,
    fetchCount: 0,
    isFetching: false,
    shouldReload: false,
    setError(e) {
        return this.set('error', e)
    },
    clearError() {
        return this.remove('error')
    },
    setFetching(isFetching = false) {
        return this.set('isFetching', isFetching)
    },
    incFetchCount() {
        return this.set('fetchCount', this.fetchCount + 1)
    },
    clearFetchCount() {
        return this.set('fetchCount', 0)
    },
    setShouldReload(shouldReload = false) {
        return this.set('shouldReload', shouldReload)
    }
})()

function Reducer({ actionTypes, extReducer }) {
    return function reducer(state, action) {
        const {
            CLEAR_ERROR,
            DOWNLOAD_REQUEST,
            DOWNLOAD_SUCCESS,
            DOWNLOAD_FAILURE
        } = actionTypes

        switch (action.type) {
            case CLEAR_ERROR:
                state = state.setError(null)
                break

            case DOWNLOAD_REQUEST: {
                state = state
                    .clearError()
                    .setFetching(true)
                    .setShouldReload(false)
                break
            }

            case DOWNLOAD_SUCCESS:
                state = state
                    .incFetchCount()
                    .setFetching(false)
                    .setShouldReload(false)
                    .setData(action.payload.data)
                break

            case DOWNLOAD_FAILURE:
                state = state
                    .incFetchCount()
                    .setFetching(false)
                    .setShouldReload(false)
                    .setError(action.payload)
                break
        }

        return extReducer ? extReducer(state, action) : state
    }
}

function useDownload(entityName, params, { doDownload, extReducer }) {
    params = useRefCurrent(params)

    const actionTypes = useMemo(() => ActionTypes(entityName), [entityName])

    const actions = useMemo(() => Actions({ actionTypes, doDownload }), [actionTypes, doDownload])

    const reducer = useMemo(() => Reducer({ actionTypes, extReducer }), [actionTypes, extReducer])

    const [state, dispatch] = useReducer(reducer, State)

    const fetch = useCallback(() => actions.download(params)(dispatch), [params, actions])

    const fetchIf = useCallback(condition => (
        condition ? fetch() : Promise.resolve()
    ), [fetch])

    const clearError = useCallback(() => dispatch(actions.clearError()), [actions])

    return { state, fetch, fetchIf, clearError, dispatch }
}

export default useDownload
