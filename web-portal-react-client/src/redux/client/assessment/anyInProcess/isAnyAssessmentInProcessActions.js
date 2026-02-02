import { ACTION_TYPES } from 'lib/Constants'

import service from 'services/AssessmentService'

const {
    CLEAR_IS_ANY_ASSESSMENT_IN_PROCESS,
    CLEAR_IS_ANY_ASSESSMENT_IN_PROCESS_ERROR,
    LOAD_IS_ANY_ASSESSMENT_IN_PROCESS_REQUEST,
    LOAD_IS_ANY_ASSESSMENT_IN_PROCESS_SUCCESS,
    LOAD_IS_ANY_ASSESSMENT_IN_PROCESS_FAILURE
} = ACTION_TYPES

export function clear () {
    return { type: CLEAR_IS_ANY_ASSESSMENT_IN_PROCESS }
}

export function clearError () {
    return { type: CLEAR_IS_ANY_ASSESSMENT_IN_PROCESS_ERROR }
}

export function load (params) {
    return dispatch => {
        dispatch({ type: LOAD_IS_ANY_ASSESSMENT_IN_PROCESS_REQUEST })
        return service.isAnyInProcess(params).then(response => {
            dispatch({ type: LOAD_IS_ANY_ASSESSMENT_IN_PROCESS_SUCCESS, payload: response.data })
            return response
        }).catch(e => {
            dispatch({ type: LOAD_IS_ANY_ASSESSMENT_IN_PROCESS_FAILURE, payload: e })
        })
    }
}
