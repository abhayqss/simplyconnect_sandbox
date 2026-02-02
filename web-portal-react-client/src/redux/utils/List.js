import { defer } from 'lib/utils/Utils'
import { ACTION_TYPES, PAGINATION } from 'lib/Constants'

import BaseState from './base/State'
import BaseActions from './base/Actions'

const { hash, Record } = require('immutable')

const { FIRST_PAGE } = PAGINATION

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA
} = ACTION_TYPES

export function ActionTypes(entity) {
    return {
        CLEAR: `CLEAR_${entity}_LIST`,
        CLEAR_DATA: `CLEAR_${entity}_LIST_DATA`,
        CLEAR_ERROR: `CLEAR_${entity}_LIST_ERROR`,
        TOGGLE_FILTER: `TOGGLE_${entity}_LIST_FILTER`,
        CLEAR_FILTER: `CLEAR_${entity}_LIST_FILTER`,
        CHANGE_FILTER: `CHANGE_${entity}_LIST_FILTER`,
        CHANGE_FILTER_FIELD: `CHANGE_${entity}_LIST_FILTER_FIELD`,
        CHANGE_SORTING: `CHANGE_${entity}_LIST_SORTING`,
        LOAD_REQUEST: `LOAD_${entity}_LIST_REQUEST`,
        LOAD_SUCCESS: `LOAD_${entity}_LIST_SUCCESS`,
        LOAD_FAILURE: `LOAD_${entity}_LIST_FAILURE`
    }
}

export function State(state = {}, opts = {}) {
    const {
        isMinimal = false,
        isPageable = true,
        isSortable = true,
        isFilterable = true
    } = opts

    let filterHashCode = hash()

    return BaseState({
        isFilterOpen: true,
        dataSource: Record({
            data: [],
            ...!isMinimal && ({
                ...isSortable && {
                    sorting: Record({
                        field: null,
                        order: null
                    })(),
                },
                ...isPageable && {
                    pagination: Record({
                        page: 1,
                        size: 15,
                        totalCount: 0
                    })(),
                },
                ...isFilterable && {
                    filter: Record({
                        searchText: ''
                    })()
                }
            })
        })(),
        clearData() {
            return this.removeIn(['dataSource', 'data'], [])
        },
        setData(data) {
            return this.setIn(['dataSource', 'data'], data)
        },
        ...!isMinimal && {
            ...isSortable && {
                setSorting(sorting) {
                    return this.mergeIn(['dataSource', 'sorting'], sorting)
                }
            },
            ...isPageable && {
                setPagination(pagination) {
                    return this.mergeIn(['dataSource', 'pagination'], pagination)
                }
            },
            ...isFilterable && {
                toggleFilter(isOpen) {
                    return this.setIn(['isFilterOpen'], isOpen)
                },
                getFilter() {
                    return this.getIn(['dataSource', 'filter'])
                },
                changeFilter(changes = {}) {
                    return this.mergeIn(['dataSource', 'filter'], changes)
                },
                changeFilterField(name, value) {
                    return this.setIn(
                        ['dataSource', 'filter', name], value
                    )
                },
                clearFilter(defaults) {
                    return this.setIn(
                        ['dataSource', 'filter'],
                        this.getIn(['dataSource', 'filter'])
                            .clear().merge(defaults)
                    )
                },
                isFilterChanged() {
                    return this.dataSource.filter.hashCode() !== filterHashCode
                },
                getFilterHashCode() {
                    return filterHashCode
                },
                updateFilterHashCode() {
                    filterHashCode = this.dataSource.filter.hashCode()
                    return this
                },
                updateFilterHashCodeIf(condition = false) {
                    return condition ? this.updateFilterHashCode() : this
                }
            }
        },
        ...state,
    })
}

export function Actions({ actionTypes = {}, doLoad, ...opts }) {
    const {
        isMinimal = false,
        isPageable = true,
        isSortable = true,
        isFilterable = true
    } = opts

    const {
        CLEAR,
        CLEAR_DATA,
        TOGGLE_FILTER,
        CLEAR_FILTER,
        CHANGE_FILTER,
        CHANGE_FILTER_FIELD,
        CHANGE_SORTING,
        LOAD_REQUEST,
        LOAD_SUCCESS,
        LOAD_FAILURE
    } = actionTypes

    let request = null
    let isRequestAborted = false

    function getRequest(o) {
        request = o
    }

    return {
        ...BaseActions({ actionTypes, doLoad }),
        clear: (defaults) => ({
            type: CLEAR, payload: { defaults }
        }),
        clearData: (shouldReload) => ({
            type: CLEAR_DATA,
            payload: { shouldReload }
        }),
        ...!isMinimal && isPageable && {
            load: opts.load || function (params = {}) {
                if (request) {
                    request.abort()

                    request = null
                    isRequestAborted = true
                }

                return dispatch => {
                    dispatch({ type: LOAD_REQUEST })

                    return doLoad(params, { getRequest }).then(response => {
                        request = null
                        isRequestAborted = false

                        const { page, size } = params
                        const { data, totalCount } = response || {}

                        dispatch({
                            type: LOAD_SUCCESS,
                            payload: {
                                data, page, size, totalCount
                            }
                        })

                        return response
                    }).catch(error => {
                        if (!isRequestAborted) {
                            dispatch({ type: LOAD_FAILURE, payload: error })
                            return { error }
                        }

                        request = null
                        isRequestAborted = false
                    })
                }
            }
        },
        ...!isMinimal && {
            ...isFilterable && {
                toggleFilter: isOpen => {
                    return { type: TOGGLE_FILTER, payload: isOpen }
                },
                clearFilter: (defaults, shouldReload, shouldUpdateHashCode) => {
                    return dispatch => {
                        return defer().then(() => {
                            dispatch({
                                type: CLEAR_FILTER,
                                payload: { defaults, shouldReload, shouldUpdateHashCode }
                            })
                        })
                    }
                },

                changeFilter: (changes, shouldReload, shouldUpdateHashCode) => {
                    return dispatch => {
                        return defer().then(() => {
                            dispatch({
                                type: CHANGE_FILTER,
                                payload: { changes, shouldReload, shouldUpdateHashCode }
                            })
                        })
                    }
                },

                changeFilterField: (name, value, shouldReload, shouldUpdateHashCode) => {
                    return dispatch => {
                        return defer().then(() => {
                            dispatch({
                                type: CHANGE_FILTER_FIELD,
                                payload: { name, value, shouldReload, shouldUpdateHashCode }
                            })
                        })
                    }
                }
            },
            ...isSortable && {
                sort: (field, order, shouldReload) => {
                    return {
                        type: CHANGE_SORTING,
                        payload: { field, order, shouldReload }
                    }
                }
            }
        }
    }
}

export function Reducer({ stateClass, actionTypes = {}, extReducer, ...opts}) {
    const {
        isMinimal = false,
        isPageable = true
    } = opts

    const {
        CLEAR,
        CLEAR_DATA,
        CLEAR_ERROR,
        TOGGLE_FILTER,
        CLEAR_FILTER,
        CHANGE_FILTER,
        CHANGE_FILTER_FIELD,
        CHANGE_SORTING,
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
            case CLEAR_ALL_AUTH_DATA: {
                const {
                    defaults
                } = action.payload ?? {}

                state = state.clear().mergeDeep(defaults ?? {})
                break
            }

            case CLEAR_ERROR:
                state = state.clearError()
                break

            case CLEAR_DATA: {
                const {
                    shouldReload
                } = action.payload

                state = state
                    .clearData()
                    .setShouldReload(shouldReload ?? true)
                break
            }

            case TOGGLE_FILTER: {
                state = state.toggleFilter(action.payload ?? false)
                break
            }

            case CLEAR_FILTER: {
                const {
                    defaults,
                    shouldReload,
                    shouldUpdateHashCode
                } = action.payload

                state = state
                    .clearFilter(defaults)
                    .setShouldReload(shouldReload ?? true)
                    .updateFilterHashCodeIf(shouldUpdateHashCode)

                if (shouldReload && isPageable) {
                    state = state.setPagination({ page: FIRST_PAGE })
                }

                break
            }

            case CHANGE_FILTER: {
                const {
                    changes,
                    shouldReload,
                    shouldUpdateHashCode
                } = action.payload

                state = state
                    .changeFilter(changes)
                    .setShouldReload(shouldReload ?? true)
                    .updateFilterHashCodeIf(shouldUpdateHashCode)

                if (shouldReload && isPageable) {
                    state = state.setPagination({ page: FIRST_PAGE })
                }

                break
            }

            case CHANGE_FILTER_FIELD: {
                const {
                    name,
                    value,
                    shouldReload = true,
                    shouldUpdateHashCode
                } = action.payload

                state = state
                    .changeFilterField(name, value)
                    .setShouldReload(shouldReload)
                    .updateFilterHashCodeIf(shouldUpdateHashCode)

                if (shouldReload && isPageable) {
                    state = state.setPagination({ page: FIRST_PAGE })
                }

                break
            }

            case CHANGE_SORTING: {
                const {
                    field, order, shouldReload = true
                } = action.payload

                state = state
                    .setSorting({ field, order })
                    .setShouldReload(shouldReload)
                break
            }

            case LOAD_REQUEST:
                state = state
                    .clearError()
                    .setFetching(true)
                    .setShouldReload(false)
                break

            case LOAD_SUCCESS: {
                const {
                    data,
                    page,
                    size,
                    totalCount
                } = action.payload

                state = state
                    .setData(data)
                    .incFetchCount()
                    .setFetching(false)

                state = !isMinimal && isPageable ? (
                    state.setPagination({
                        page, size, totalCount
                    })
                ) : state

                break
            }

            case LOAD_FAILURE:
                state = state
                    .incFetchCount()
                    .setFetching(false)
                    .setError(action.payload)
                break
        }

        return extReducer ? extReducer(state, action) : state
    }
}