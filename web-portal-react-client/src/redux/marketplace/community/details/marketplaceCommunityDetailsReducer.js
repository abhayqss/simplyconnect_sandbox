import { ACTION_TYPES } from 'lib/Constants'

import savingActionTypes from '../saving/marketplaceCommunitySavingActionTypes'
import removingActionTypes from '../removing/marketplaceCommunityRemovingActionTypes'

import InitialState from './MarketplaceCommunityDetailsInitialState'

const {
    CLEAR_MARKETPLACE_COMMUNITY_DETAILS,
    CLEAR_MARKETPLACE_COMMUNITY_DETAILS_ERROR,

    LOAD_MARKETPLACE_COMMUNITY_DETAILS_REQUEST,
    LOAD_MARKETPLACE_COMMUNITY_DETAILS_SUCCESS,
    LOAD_MARKETPLACE_COMMUNITY_DETAILS_FAILURE
} = ACTION_TYPES

const {
    SEND_SUCCESS: MARKETPLACE_COMMUNITY_SAVING_SUCCESS
} = savingActionTypes

const {
    SEND_SUCCESS: MARKETPLACE_COMMUNITY_REMOVING_SUCCESS
} = removingActionTypes

const initialState = new InitialState()

export default function marketplaceCommunityDetailsReducer (state = initialState, action) {
    if (!(state instanceof InitialState)) {
        return initialState.mergeDeep(state)
    }

    switch (action.type) {
        case CLEAR_MARKETPLACE_COMMUNITY_DETAILS:
            return state.removeIn(['data'])
                .removeIn(['error'])
                .setIn(['isFetching'], false)
                .setIn(['shouldReload'], false)

        case CLEAR_MARKETPLACE_COMMUNITY_DETAILS_ERROR:
            return state.removeIn(['error'])

        case LOAD_MARKETPLACE_COMMUNITY_DETAILS_REQUEST:
            return state.setIn(['isFetching'], true)
                .setIn(['shouldReload'], false)
                .setIn(['error'], null)

        case LOAD_MARKETPLACE_COMMUNITY_DETAILS_SUCCESS:
            return state.setIn(['isFetching'], false)
                .setIn(['data'], action.payload)

        case LOAD_MARKETPLACE_COMMUNITY_DETAILS_FAILURE:
            return state.setIn(['isFetching'], false)
                .setIn(['error'], action.payload)

        case MARKETPLACE_COMMUNITY_SAVING_SUCCESS: {
            const { communityId } = action.payload.community

            if (state.data?.id === communityId) {
                return state.setIn(['data'], {
                    ...state.data,
                    marketplace: {
                        ...state.data?.marketplace,
                        isSaved: true
                    }
                })
            } else break
        }

        case MARKETPLACE_COMMUNITY_REMOVING_SUCCESS: {
            const { communityId } = action.payload.community

            if (state.data?.id === communityId) {
                return state.setIn(['data'], {
                    ...state.data,
                    marketplace: {
                        ...state.data?.marketplace,
                        isSaved: false
                    }
                })
            } else break
        }
    }

    return state
}
