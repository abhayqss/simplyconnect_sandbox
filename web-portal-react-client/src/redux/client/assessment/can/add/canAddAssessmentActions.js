import { ACTION_TYPES } from 'lib/Constants'

import service from 'services/AssessmentService'

const {
    CLEAR_CAN_ADD_ASSESSMENT,
    CLEAR_CAN_ADD_ASSESSMENT_ERROR,
    LOAD_CAN_ADD_ASSESSMENT_REQUEST,
    LOAD_CAN_ADD_ASSESSMENT_SUCCESS,
    LOAD_CAN_ADD_ASSESSMENT_FAILURE,
} = ACTION_TYPES

export function clear () {
    return { type: CLEAR_CAN_ADD_ASSESSMENT }
}

export function clearError () {
    return { type: CLEAR_CAN_ADD_ASSESSMENT_ERROR }
}

export function load (params) {
    return dispatch => {
        dispatch({ type: LOAD_CAN_ADD_ASSESSMENT_REQUEST })
        return service.canAdd(params).then(response => {
            dispatch({ type: LOAD_CAN_ADD_ASSESSMENT_SUCCESS, payload: response.data })
            return response
        }).catch(e => {
            dispatch({ type: LOAD_CAN_ADD_ASSESSMENT_FAILURE, payload: e })
        })
    }
}
