import { ACTION_TYPES } from 'lib/Constants'

import service from 'services/CareTeamMemberService'

const {
    CLEAR_CARE_TEAM_MEMBER_LIST_ERROR,

    CLEAR_CARE_TEAM_MEMBER_LIST,
    CLEAR_CARE_TEAM_MEMBER_LIST_FILTER,
    CHANGE_CARE_TEAM_MEMBER_LIST_FILTER,

    LOAD_CARE_TEAM_MEMBER_LIST_REQUEST,
    LOAD_CARE_TEAM_MEMBER_LIST_SUCCESS,
    LOAD_CARE_TEAM_MEMBER_LIST_FAILURE,

    LOAD_DELETE_TEAM_MEMBER_DOCUMENT_REQUEST,
    LOAD_DELETE_TEAM_MEMBER_DOCUMENT_SUCCESS,
    LOAD_DELETE_TEAM_MEMBER_DOCUMENT_FAILURE,
    CHANGE_CARE_TEAM_MEMBER_LIST_SORTING
} = ACTION_TYPES

export function clear () {
    return { type: CLEAR_CARE_TEAM_MEMBER_LIST }
}

export function clearError () {
    return { type: CLEAR_CARE_TEAM_MEMBER_LIST_ERROR }
}

export function sort(field, order, shouldReload) {
    return {
        type:  CHANGE_CARE_TEAM_MEMBER_LIST_SORTING,
        payload: { field, order, shouldReload }
    }
}

export function clearFilter () {
    return { type: CLEAR_CARE_TEAM_MEMBER_LIST_FILTER }
}

export function changeFilter (changes, shouldReload) {
    return {
        type: CHANGE_CARE_TEAM_MEMBER_LIST_FILTER,
        payload: { changes, shouldReload }
    }
}

export function load (config) {
    return dispatch => {
        dispatch({ type: LOAD_CARE_TEAM_MEMBER_LIST_REQUEST, payload: config.page })

        return service.find(config).then(response => {
            const { page, size } = config
            const { data, totalCount } = response

            dispatch({
                type: LOAD_CARE_TEAM_MEMBER_LIST_SUCCESS,
                payload: { data, page, size, totalCount }
            })

            return response
        }).catch(e => {
            dispatch({ type: LOAD_CARE_TEAM_MEMBER_LIST_FAILURE, payload: e })
        })
    }
}

export function deleteMember(careTeamMemberId) {
    return async dispatch => {
        dispatch({ type: LOAD_DELETE_TEAM_MEMBER_DOCUMENT_REQUEST })

        try {
            await service.deleteById(careTeamMemberId)

            dispatch({ type: LOAD_DELETE_TEAM_MEMBER_DOCUMENT_SUCCESS, payload: { careTeamMemberId } })
        } catch (e) {
            dispatch({ type: LOAD_DELETE_TEAM_MEMBER_DOCUMENT_FAILURE, payload: e })
        }
    }
}
