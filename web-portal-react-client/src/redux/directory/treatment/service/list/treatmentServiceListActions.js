import { ACTION_TYPES } from 'lib/Constants'

import service from 'services/DirectoryService'

const {
    CLEAR_TREATMENT_SERVICE_LIST,
    LOAD_TREATMENT_SERVICE_LIST_REQUEST,
    LOAD_TREATMENT_SERVICE_LIST_SUCCESS,
    LOAD_TREATMENT_SERVICE_LIST_FAILURE
} = ACTION_TYPES

export function clear () {
    return { type: CLEAR_TREATMENT_SERVICE_LIST }
}

export function load (params, shouldDispatch = true) {
    return dispatch => {
        shouldDispatch && dispatch({
            type: LOAD_TREATMENT_SERVICE_LIST_REQUEST
        })

        return service.findTreatmentServices(params).then(response => {
            shouldDispatch && dispatch({
                type: LOAD_TREATMENT_SERVICE_LIST_SUCCESS,
                payload: { data: response.data }
            })

            return response
        }).catch(e => {
            shouldDispatch && dispatch({
                type: LOAD_TREATMENT_SERVICE_LIST_FAILURE,
                payload: e
            })
        })
    }
}

