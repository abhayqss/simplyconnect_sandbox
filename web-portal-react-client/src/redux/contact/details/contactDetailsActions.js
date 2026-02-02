import { ACTION_TYPES } from 'lib/Constants'

import service from 'services/ContactService'
import adminAssociationsService from "services/AssociationsService";
import adminVendorService from "../../../services/AdminVendorService";


const {
    CLEAR_CONTACT_DETAILS,
    CLEAR_CONTACT_DETAILS_ERROR,

    LOAD_CONTACT_DETAILS_REQUEST,
    LOAD_CONTACT_DETAILS_SUCCESS,
    LOAD_CONTACT_DETAILS_FAILURE
} = ACTION_TYPES

export function clear () {
    return {
        type: CLEAR_CONTACT_DETAILS
    }
}

export function clearError () {
    return {
        type: CLEAR_CONTACT_DETAILS_ERROR
    }
}

export function load (contactId, shouldNotSave) {
    return dispatch => {
        dispatch({ type: LOAD_CONTACT_DETAILS_REQUEST })
        return service.findById(contactId).then(response => {
            const { data } = response
            dispatch({ type: LOAD_CONTACT_DETAILS_SUCCESS, payload: { data, shouldNotSave } })
            return response
        }).catch((e) => {
            dispatch({ type: LOAD_CONTACT_DETAILS_FAILURE, payload: e })
        })
    }
}

export function loadAssociation (contactId, shouldNotSave) {
    return dispatch => {
        dispatch({ type: LOAD_CONTACT_DETAILS_REQUEST })
        return adminAssociationsService.getAssociationContactData(contactId).then(response => {
            const { data } = response
            dispatch({ type: LOAD_CONTACT_DETAILS_SUCCESS, payload: { data, shouldNotSave } })
            return response
        }).catch((e) => {
            dispatch({ type: LOAD_CONTACT_DETAILS_FAILURE, payload: e })
        })
    }
}

export function loadVendorContact (contactId, shouldNotSave) {
    return dispatch => {
        dispatch({ type: LOAD_CONTACT_DETAILS_REQUEST })
        return adminVendorService.getVendorContactData(contactId).then(response => {
            const { data } = response
            dispatch({ type: LOAD_CONTACT_DETAILS_SUCCESS, payload: { data, shouldNotSave } })
            return response
        }).catch((e) => {
            dispatch({ type: LOAD_CONTACT_DETAILS_FAILURE, payload: e })
        })
    }
}
