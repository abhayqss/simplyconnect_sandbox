import { ACTION_TYPES } from 'lib/Constants'

import service from 'services/CareTeamMemberService'

const {
    CLEAR_CAN_VIEW_CARE_TEAM_MEMBER,
    CLEAR_CAN_VIEW_CARE_TEAM_MEMBER_ERROR,
    LOAD_CAN_VIEW_CARE_TEAM_MEMBER_REQUEST,
    LOAD_CAN_VIEW_CARE_TEAM_MEMBER_SUCCESS,
    LOAD_CAN_VIEW_CARE_TEAM_MEMBER_FAILURE,
} = ACTION_TYPES

export function clear() {
    return { type: CLEAR_CAN_VIEW_CARE_TEAM_MEMBER }
}

export function clearError() {
    return { type: CLEAR_CAN_VIEW_CARE_TEAM_MEMBER_ERROR }
}

export function load(params) {
    return dispatch => {
        dispatch({ type: LOAD_CAN_VIEW_CARE_TEAM_MEMBER_REQUEST })
        return service.canView(params).then(response => {
            dispatch({ type: LOAD_CAN_VIEW_CARE_TEAM_MEMBER_SUCCESS, payload: response.data })
            return response
        }).catch(e => {
            dispatch({ type: LOAD_CAN_VIEW_CARE_TEAM_MEMBER_FAILURE, payload: e })
        })
    }
}
