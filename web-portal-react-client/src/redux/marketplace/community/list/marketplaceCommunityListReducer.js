import { each } from 'underscore'

import { Reducer } from 'redux/utils/List'

import { PAGINATION } from 'lib/Constants'

import InitialState from './MarketplaceCommunityListInitialState'

import actionTypes from './marketplaceCommunityListActionTypes'

import savingActionTypes from '../saving/marketplaceCommunitySavingActionTypes'
import removingActionTypes from '../removing/marketplaceCommunityRemovingActionTypes'

const { FIRST_PAGE } = PAGINATION

const {
    LOAD_REQUEST,
    LOAD_SUCCESS,
    LOAD_FAILURE,
} = actionTypes

const {
    SEND_REQUEST: SEND_MARKETPLACE_COMMUNITY_SAVING_REQUEST,
    SEND_FAILURE: SEND_MARKETPLACE_COMMUNITY_SAVING_FAILURE
} = savingActionTypes

const {
    SEND_REQUEST: SEND_MARKETPLACE_COMMUNITY_REMOVING_REQUEST,
    SEND_FAILURE: SEND_MARKETPLACE_COMMUNITY_REMOVING_FAILURE
} = removingActionTypes

export default Reducer({
    actionTypes,
    stateClass: InitialState,
    extReducer: (state, action) => {
        switch (action.type) {
            case LOAD_REQUEST: {
                const { page, shouldAccumulate } = action.payload

                const path = shouldAccumulate && page !== FIRST_PAGE ? (
                    ['dataSource', 'pagination', 'isFetching']
                ) : ['dataSource', 'isFetching']

                return state.setIn(path, true)
            }

            case LOAD_SUCCESS: {
                const {
                    page,
                    prevData,
                    shouldAccumulate
                } = action.payload

                let data = action.payload.data

                data = !shouldAccumulate || page === FIRST_PAGE ? data : [...prevData, ...data]

                return state
                    .setIn(['dataSource', 'data'], data)
                    .setIn(['dataSource', 'isFetching'], false)
                    .setIn(['dataSource', 'pagination', 'isFetching'], false)
            }

            case LOAD_FAILURE: {
                return state
                    .setIn(['dataSource', 'isFetching'], false)
                    .setIn(['dataSource', 'pagination', 'isFetching'], false)
            }

            case SEND_MARKETPLACE_COMMUNITY_SAVING_REQUEST:
            case SEND_MARKETPLACE_COMMUNITY_REMOVING_FAILURE: {
                const {
                    community
                } = action.payload

                const data = state.dataSource.data || []

                each(data, o => {
                    if (o.communityId === community.communityId) {
                        o.isSaved = true
                    }
                })

                return state.setIn(['dataSource', 'data'], [...data])
            }

            case SEND_MARKETPLACE_COMMUNITY_SAVING_FAILURE:
            case SEND_MARKETPLACE_COMMUNITY_REMOVING_REQUEST: {
                const {
                    community
                } = action.payload

                const data = state.dataSource.data || []

                each(data, o => {
                    if (o.communityId === community.communityId) {
                        o.isSaved = false
                    }
                })

                return state.setIn(['dataSource', 'data'], [...data])
            }
        }

        return state
    }
})