import { Reducer } from 'redux/utils/List'

import actionTypes from './savedMarketplaceCommunityListActionTypes'
import InitialState from './SavedMarketplaceCommunityListInitialState'

import savingActionTypes from '../../saving/marketplaceCommunitySavingActionTypes'
import removingActionTypes from '../../removing/marketplaceCommunityRemovingActionTypes'

const {
    SEND_SUCCESS: SEND_MARKETPLACE_COMMUNITY_SAVING_SUCCESS
} = savingActionTypes

const {
    SEND_SUCCESS: SEND_MARKETPLACE_COMMUNITY_REMOVING_SUCCESS
} = removingActionTypes

export default Reducer({
    actionTypes,
    stateClass: InitialState,
    extReducer: (state, action) => {
        switch (action.type) {
            case SEND_MARKETPLACE_COMMUNITY_SAVING_SUCCESS: {
                const {
                    community
                } = action.payload

                return state.setIn(
                    ['dataSource', 'data'],
                    state.dataSource.data
                        .push(community)
                        .sortBy(o => o.organizationName)
                )
            }

            case SEND_MARKETPLACE_COMMUNITY_REMOVING_SUCCESS: {
                const {
                    community
                } = action.payload

                return state.setIn(
                    ['dataSource', 'data'],
                    state.dataSource.data.filter(
                        o => o.communityId !== community.communityId
                    )
                )
            }

            case actionTypes.LOAD_SUCCESS: {
                return state.setIn(
                    ['dataSource', 'data'],
                    state.dataSource.data.push(...action.payload)
                )
            }
        }

        return state
    }
})