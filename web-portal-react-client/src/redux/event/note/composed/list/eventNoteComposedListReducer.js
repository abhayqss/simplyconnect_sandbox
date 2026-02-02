import { Reducer } from 'redux/utils/List'

import actionTypes from './actionTypes'
import InitialState from './EventNoteComposedListInitialState'

const { LOAD_EVENT_PAGE_NUMBER_REQUEST } = actionTypes

export default Reducer({
    actionTypes,
    stateClass: InitialState,
    extReducer: (state, action) => {
        if (action.type === LOAD_EVENT_PAGE_NUMBER_REQUEST) {
            const {
                clientId,
                communityIds,
                organizationId
            } = state.dataSource.filter

            return state.clearFilter({
                clientId,
                communityIds,
                organizationId
            })
        }

        return state
    }
})