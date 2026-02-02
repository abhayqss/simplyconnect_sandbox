import { useCallback } from 'react'

import useList from 'hooks/common/useList'

import service from 'services/LabResearchOrderService'

const { Record } = require('immutable')

const TOGGLE_CHECKBOX = 'TOGGLE_CHECKBOX'
const TOGGLE_ALL_CHECKBOX = 'TOGGLE_ALL_CHECKBOX'
const LOAD_SUCCESS = 'LOAD_PENDING_REVIEW_ORDER_LIST_SUCCESS'

const initialState = {
    dataSource: Record({
        data: [],
    })(),
    getSelectedCount() {
        return this.dataSource.data.filter(o => o.selected).length
    },
    isChanged() {
        return this.dataSource.data.some(o => o.selected)
    },
    isAllSelected() {
        return this.getSelectedCount() === this.dataSource.data.length
    }
}


const options = {
    doLoad: ({ communityIds, organizationId }) => service.findPendingReviewOrders({ communityIds, organizationId }),
    isMinimal: true,
    initialState,
    extReducer: (state, action) => {
        switch (action.type) {
            case LOAD_SUCCESS: {
                const { data } = state.dataSource

                return state
                    .setIn(
                        ['dataSource', 'data'],
                        data.map(o => ({
                            ...o,
                            selected: false
                        }))
                    )
            }

            case TOGGLE_CHECKBOX: {
                const { data } = state.dataSource
                const { id, value } = action.payload

                let index = data.findIndex(o => o.id === id)

                return state.setIn(
                    ['dataSource', 'data', index],
                    { ...data[index], selected: value }
                )
            }

            case TOGGLE_ALL_CHECKBOX: {
                const { data } = state.dataSource

                return state.setIn(
                    ['dataSource', 'data'],
                    data.map(o => ({ ...o, selected: !state.isAllSelected() }))
                )
            }

            default: return state
        }
    }
}

function useNetworkList(params) {
    const { state, fetch, dispatch } = useList('PENDING_REVIEW_ORDER', params, options)

    const toggleSelected = useCallback((id, value) => dispatch({
        type: TOGGLE_CHECKBOX,
        payload: { id, value }
    }), [dispatch])

    const toggleAllSelected = useCallback((id, value) => dispatch({
        type: TOGGLE_ALL_CHECKBOX,
        payload: { id, value }
    }), [dispatch])

    return { state, fetch, toggleSelected, toggleAllSelected }
}

export default useNetworkList
