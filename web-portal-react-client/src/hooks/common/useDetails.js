import { useMemo, useReducer, useCallback } from 'react'
import useRefCurrent from "./useRefCurrent";

const { Record } = require('immutable')

function ActionTypes(entity) {
    return {
        CLEAR: `CLEAR_${entity}_DETAILS`,
        REFRESH: `REFRESH_${entity}_DETAILS`,
        CLEAR_ERROR: `CLEAR_${entity}_DETAILS_ERROR`,
        LOAD_REQUEST: `LOAD_${entity}_DETAILS_REQUEST`,
        LOAD_SUCCESS: `LOAD_${entity}_DETAILS_SUCCESS`,
        LOAD_FAILURE: `LOAD_${entity}_DETAILS_FAILURE`,
    }
}

function Actions({ actionTypes, doLoad }) {
    const {
        CLEAR,
        REFRESH,
        CLEAR_ERROR,
        LOAD_REQUEST,
        LOAD_SUCCESS,
        LOAD_FAILURE,
    } = actionTypes

    return {
        clear: () => ({ type: CLEAR }),
        clearError: () => ({ type: CLEAR_ERROR }),
        load: (...args) => {
            return dispatch => {
                dispatch({ type: LOAD_REQUEST })

                return doLoad(...args).then(response => {
                    dispatch({ type: LOAD_SUCCESS, payload: { data: response.data } })

                    return response
                }).catch(error => {
                    dispatch({ type: LOAD_FAILURE, payload: error })

                    return { error }
                })
            }
        },
        refresh: () => ({ type: REFRESH })
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
            CLEAR,
            REFRESH,
            CLEAR_ERROR,
            LOAD_REQUEST,
            LOAD_SUCCESS,
            LOAD_FAILURE,
        } = actionTypes

        switch (action.type) {
            case CLEAR:
                state = state.clear()
                break

            case CLEAR_ERROR:
                state = state.setError(null)
                break

            case REFRESH:
                return state.setShouldReload(true)

            case LOAD_REQUEST: {
                state = state
                    .clearError()
                    .setFetching(true)
                    .setShouldReload(false)
                break
            }

            case LOAD_SUCCESS:
                state = state
                    .incFetchCount()
                    .setFetching(false)
                    .setShouldReload(false)
                    .setData(action.payload.data)
                break

            case LOAD_FAILURE:
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

/*
* Be careful! When "params" object is changed - fetch and fetchIf changes too
* */
function useDetails(entityName, params, { doLoad, extReducer }) {
    const actionTypes = useMemo(() => ActionTypes(entityName), [entityName])

    const actions = useMemo(() => Actions({ actionTypes, doLoad }), [actionTypes, doLoad])

    const reducer = useMemo(() => Reducer({ actionTypes, extReducer }), [actionTypes, extReducer])

    const [state, dispatch] = useReducer(reducer, State)

    const fetch = useCallback(() => actions.load(params)(dispatch), [params, actions])

    const fetchIf = useCallback(condition => (
        condition ? fetch() : Promise.resolve()
    ), [fetch])

    const refresh = useCallback(() => dispatch(actions.refresh()), [actions])

    const clearError = useCallback(() => dispatch(actions.clearError()), [actions])

    return { state, fetch, fetchIf, refresh, clearError, dispatch }
}

export default useDetails
