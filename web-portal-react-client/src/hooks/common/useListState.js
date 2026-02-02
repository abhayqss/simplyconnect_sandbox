import {
    useMemo,
    useReducer,
    useCallback
} from 'react'

import {
    isFunction
} from 'underscore'

const {
    Record,
    isImmutable,
    isValueObject
} = require('immutable')

const SET_ERROR = 'SET_ERROR'
const CLEAR_ERROR = 'CLEAR_ERROR'
const SET_FILTER = 'SET_FILTER'
const CHANGE_FILTER = 'CHANGE_FILTER'
const CLEAR_FILTER = 'CLEAR_FILTER'
const RESET_FILTER = 'RESET_FILTER'
const SET_SORTING = 'SET_SORTING'
const SET_PAGINATION = 'SET_PAGINATION'

const Immutable = o => {
    if (isFunction(o)) return o()
    if (isValueObject(o)) Record(o)()
    if (isImmutable(o)) return o
    return Record({})()
}

function State(filterEntity) {
    return Record({
        error: null,
        filter: Immutable(filterEntity),
        sorting: Record({
            field: '',
            order: 'asc'
        })(),
        pagination: Record({
            page: 1,
            size: 15,
            totalCount: 0
        })()
    })
}

function Actions() {
    return {
        setError(e) {
            return { type: SET_ERROR, payload: e }
        },
        clearError() {
            return { type: CLEAR_ERROR }
        },
        setFilter(data) {
            return { type: SET_FILTER, payload: data }
        },
        changeFilter(changes) {
            return { type: CHANGE_FILTER, payload: changes }
        },
        clearFilter() {
            return { type: CLEAR_FILTER }
        },
        resetFilter(data) {
            return { type: RESET_FILTER, payload: data }
        },
        setSorting(sorting) {
            return { type: SET_SORTING, payload: sorting }
        },
        setPagination(pagination) {
            return { type: SET_PAGINATION, payload: pagination }
        }
    }
}

function Reducer({ stateClass }) {
    const initialState = stateClass()

    return function (state = initialState, action) {
        if (!(state instanceof stateClass)) {
            return initialState.mergeDeep(state)
        }

        const { type, payload } = action

        switch (type) {
            case SET_ERROR:
                return state.set('error', payload)

            case CLEAR_ERROR:
                return state.set('error', null)

            case SET_FILTER:
                return state.setIn(['filter'], payload)

            case CHANGE_FILTER:
                return state.mergeIn(['filter'], payload)

            case CLEAR_FILTER:
                return state.set('filter', state.filter.clear())

            case RESET_FILTER:
                return state
                    .set('filter', state.filter.clear())
                    .mergeIn(['filter'], payload)

            case SET_SORTING:
                return state.mergeDeep({ sorting: payload })

            case SET_PAGINATION:
                return state.mergeDeep({ pagination: payload })
        }

        return state
    }
}

function useListState({ filterEntity } = {}) {
    const actions = useMemo(() => Actions(), [])
    const stateClass = useMemo(() => State(filterEntity), [filterEntity])
    const reducer = useMemo(() => Reducer({ stateClass }), [stateClass])

    const [ state, dispatch ] = useReducer(reducer, stateClass())

    const setError = useCallback(
        e => dispatch(actions.setError(e)),
        [actions]
    )

    const clearError = useCallback(
        () => dispatch(actions.clearError()),
        [actions]
    )

    const setFilter = useCallback(
        data => dispatch(actions.setFilter(data)),
        [actions]
    )

    const changeFilter = useCallback(
        changes => dispatch(actions.changeFilter(changes)),
        [actions]
    )

    const clearFilter = useCallback(
        () => dispatch(actions.clearFilter()),
        [actions]
    )

    const resetFilter = useCallback(
        data => dispatch(actions.resetFilter(data)),
        [actions]
    )

    const setSorting = useCallback(
        o => dispatch(actions.setSorting(o)),
        [actions]
    )

    const setPagination = useCallback(
        o => dispatch(actions.setPagination(o)),
        [actions]
    )

    return {
        state,
        dispatch,
        setError,
        clearError,
        setFilter,
        changeFilter,
        clearFilter,
        resetFilter,
        setSorting,
        setPagination
    }
}

export default useListState
