import {
    useMemo,
    useReducer,
    useCallback
} from 'react'

import { useRefCurrent } from 'hooks/common/index'

import { useListDataFetch } from 'hooks/common/redux'

import { State, Actions, Reducer, ActionTypes } from 'redux/utils/List'

const defaultState = {}

function useList(entityName, params, {
    doLoad,
    extReducer,
    initialState = defaultState,
    ...other
} = {}) {
    other = useRefCurrent(other)

    const actionTypes = useMemo(
        () => ActionTypes(entityName),
        [ entityName ]
    )

    const stateClass = useMemo(
        () => State(initialState, other),
        [ initialState, other ]
    )

    const actions = useMemo(() => Actions(
        { actionTypes, doLoad, ...other }
    ), [ actionTypes, doLoad, other ])

    const reducer = useMemo(() => Reducer(
        { stateClass, actionTypes, extReducer, ...other }
    ), [ actionTypes, stateClass, other, extReducer ])

    const [ state, dispatch ] = useReducer(
        reducer, stateClass()
    )

    const load = useCallback(
        params => actions.load(params)(dispatch),
        [ actions ]
    )

    const sort = useCallback(
        (...args) => dispatch(actions.sort(...args)),
        [actions]
    )

    const { fetch, fetchIf } = useListDataFetch(
        state,
        useMemo(() => ({ load }), [load]),
        params
    )

    const clear = useCallback(
        () => dispatch(actions.clear()),
        [actions]
    )

    const clearError = useCallback(
        () => dispatch(actions.clearError()),
        [actions]
    )

    return {
        state,

        sort,
        fetch,
        clear,
        fetchIf,
        dispatch,
        clearError
    }
}

export default useList
