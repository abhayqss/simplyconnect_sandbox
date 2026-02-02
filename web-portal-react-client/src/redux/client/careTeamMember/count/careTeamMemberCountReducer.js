import {ACTION_TYPES} from 'lib/Constants'

import InitialState from './CareTeamMemberCountInitialState'

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA,

    CLEAR_CARE_TEAM_MEMBER_COUNT,
    CLEAR_CARE_TEAM_MEMBER_COUNT_ERROR,
    LOAD_CARE_TEAM_MEMBER_COUNT_REQUEST,
    LOAD_CARE_TEAM_MEMBER_COUNT_SUCCESS,
    LOAD_CARE_TEAM_MEMBER_COUNT_FAILURE
} = ACTION_TYPES

const initialState = new InitialState()

export default function memberCountReducer (state = initialState, action) {
    if (!(state instanceof InitialState)) {
        return initialState.mergeDeep(state)
    }

    switch (action.type) {
        case LOGOUT_SUCCESS:
        case CLEAR_ALL_AUTH_DATA:
        case CLEAR_CARE_TEAM_MEMBER_COUNT:
            return state.removeIn(['error'])
                .removeIn(['value'])
                .removeIn(['isFetching'])
                .removeIn(['fetchCount'])
                .setIn(['shouldReload'], action.payload || false)

        case CLEAR_CARE_TEAM_MEMBER_COUNT_ERROR:
            return state.removeIn(['error'])

        case LOAD_CARE_TEAM_MEMBER_COUNT_REQUEST: {
            return state.setIn(['error'], null)
                .setIn(['isFetching'], true)
                .setIn(['shouldReload'], false)
        }

        case LOAD_CARE_TEAM_MEMBER_COUNT_SUCCESS:
            return state.removeIn(['error'])
                .setIn(['isFetching'], false)
                .setIn(['value'], action.payload)
                .setIn(['fetchCount'], state.fetchCount + 1)

        case LOAD_CARE_TEAM_MEMBER_COUNT_FAILURE:
            return state.setIn(['error'], action.payload)
                .setIn(['isFetching'], false)
                .setIn(['shouldReload'], false)
                .setIn(['fetchCount'], state.fetchCount + 1)
    }

    return state
}