import { ACTION_TYPES, CARE_TEAM_AFFILIATION_TYPES } from 'lib/Constants'

import service from 'services/CareTeamMemberService'

const {
    CLEAR_CAN_ADD_CARE_TEAM_MEMBER,
    CLEAR_CAN_ADD_CARE_TEAM_MEMBER_ERROR,
    LOAD_CAN_ADD_CARE_TEAM_MEMBER_REQUEST,
    LOAD_CAN_ADD_CARE_TEAM_MEMBER_SUCCESS,
    LOAD_CAN_ADD_CARE_TEAM_MEMBER_FAILURE,
} = ACTION_TYPES

const { REGULAR } = CARE_TEAM_AFFILIATION_TYPES

export function clear() {
    return { type: CLEAR_CAN_ADD_CARE_TEAM_MEMBER }
}

export function clearError() {
    return { type: CLEAR_CAN_ADD_CARE_TEAM_MEMBER_ERROR }
}

export function load(params) {
    return dispatch => {
        dispatch({ type: LOAD_CAN_ADD_CARE_TEAM_MEMBER_REQUEST })
        return service.canAdd({ affiliation: REGULAR, ...params }).then(response => {
            dispatch({ type: LOAD_CAN_ADD_CARE_TEAM_MEMBER_SUCCESS, payload: response.data })
            return response
        }).catch(e => {
            dispatch({ type: LOAD_CAN_ADD_CARE_TEAM_MEMBER_FAILURE, payload: e })
        })
    }
}
