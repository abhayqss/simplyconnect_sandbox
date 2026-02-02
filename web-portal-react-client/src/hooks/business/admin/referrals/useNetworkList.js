import { useCallback } from 'react'

import { omit } from 'underscore'

import useList from 'hooks/common/useList'

import service from 'services/DirectoryService'

const { Record, Set } = require('immutable')

const initialState = {
    dataSource: Record({
        data: [],
        pagination: Record({
            page: 1,
            size: 4,
            totalCount: 0
        })(),
        getData() {
            const { page, size } = this.pagination

            const from = page * size - size
            const to = from + size

            return this.data.slice(from, to)
        },
    })(),
    cache: {},
    selected: {},
}

const getSelected = data => data ? data.reduce((accum, current) => {
    accum[current.id] = current.communities.map(o => o.id)

    return accum
}, {}) : {}

const CHANGE_PAGE = 'CHANGE_PAGE'
const REMOVE_ORGANIZATION = 'REMOVE_ORGANIZATION'
const LOAD_SUCCESS = 'LOAD_NETWORK_LIST_SUCCESS'

const SELECT_COMMUNITIES = 'SELECT_COMMUNITIES'
const RESET = 'RESET_NETWORK_LIST'

const options = {
    initialState,
    doLoad: ({ communityId, serviceIds }) => service.findReferralNetworks({ communityId, serviceIds }),
    extReducer: (state, action) => {
        switch (action.type) {
            case CHANGE_PAGE:
                return state.setPagination({
                    page: action.payload.page
                })

            case REMOVE_ORGANIZATION: {
                let { id } = action.payload
                let data = state.dataSource.data.filter(o => o.id !== id)

                return state
                    .setData(data)
                    .setPagination({
                        totalCount: data.length
                    })
                    .set('selected', omit(state.selected, id))
            }

            case LOAD_SUCCESS: {
                const { data } = state.dataSource
                const totalCount = data.length

                return state
                    .setPagination({ totalCount })
                    .setIn(['hasFetched'], true)
                    .set('selected', getSelected(data))
                    .set('cache', action.payload)
            }

            case SELECT_COMMUNITIES: {
                const { id, communities } = action.payload

                return state.setIn(['selected', id], communities)
            }

            case RESET: {
                const {
                    data,
                    page,
                    size,
                    totalCount
                } = state.cache

                return state
                    .setData(data)
                    .set('selected', getSelected(data))
                    .setPagination({
                        page, size, totalCount
                    })
            }
        }

        return state
    }
}

function useNetworkList(params) {
    const { state, fetch, fetchIf, dispatch } = useList('NETWORK', params, options)

    const changePage = useCallback(page => dispatch({
        type: CHANGE_PAGE,
        payload: { page }
    }), [dispatch])

    const removeOrganization = useCallback(id => dispatch({
        type: REMOVE_ORGANIZATION,
        payload: { id }
    }), [dispatch])

    const selectCommunities = useCallback((id, communities) => dispatch({
        type: SELECT_COMMUNITIES,
        payload: { id, communities }
    }), [dispatch])

    const reset = useCallback(() => dispatch({ type: RESET }), [dispatch])

    return { state, reset, fetch, fetchIf, changePage, removeOrganization, selectCommunities }
}

export default useNetworkList
