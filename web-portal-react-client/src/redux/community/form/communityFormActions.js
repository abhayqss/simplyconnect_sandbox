import { ACTION_TYPES } from 'lib/Constants'

import { promise, allAreTrue } from 'lib/utils/Utils'

import service from 'services/CommunityService'
import legalInfoValidator from 'validators/CommunityFormLegalInfoValidator'
import marketplaceValidator from 'validators/CommunityFormMarketplaceValidator'

const {
    CLEAR_COMMUNITY_FORM,
    CLEAR_COMMUNITY_FORM_ERROR,
    CLEAR_CERTIFICATE_FORM_FIELDS,
    CLEAR_COMMUNITY_FORM_FIELD_ERROR,

    CHANGE_COMMUNITY_FORM_TAB,

    CHANGE_COMMUNITY_FORM_FIELD,
    CHANGE_COMMUNITY_FORM_FIELDS,
    CHANGE_COMMUNITY_FORM_FIELD_DEEP,
    CHANGE_COMMUNITY_FORM_MARKET_PLACE_FIELD,
    ADD_COMMUNITY_FORM_REFERRAL_EMAILS_FIELD,
    SET_INITIAL_COMMUNITY_FORM_REFERRAL_EMAILS_FIELD,

    VALIDATE_COMMUNITY_FORM_LEGAL_INFO,
    VALIDATE_COMMUNITY_FORM_LEGAL_INFO_FIELD,

    VALIDATE_COMMUNITY_FORM_MARKETPLACE,
    VALIDATE_COMMUNITY_FORM_AFFILIATE_RELATIONSHIP,

    VALIDATE_COMMUNITY_DATA_UNIQ_REQUEST,
    VALIDATE_COMMUNITY_DATA_UNIQ_SUCCESS,
    VALIDATE_COMMUNITY_DATA_UNIQ_FAILURE,

    SAVE_COMMUNITY_REQUEST,
    SAVE_COMMUNITY_SUCCESS,
    SAVE_COMMUNITY_FAILURE,

    LOAD_SERVER_SELF_TRUSTED_CERT_REQUEST,
    LOAD_SERVER_SELF_TRUSTED_CERT_SUCCESS,
    LOAD_SERVER_SELF_TRUSTED_CERT_FAILURE,

    VALIDATE_COMMUNITY_BUSINESS_UNIT_CODES_REQUEST,
    VALIDATE_COMMUNITY_BUSINESS_UNIT_CODES_SUCCESS,
    VALIDATE_COMMUNITY_BUSINESS_UNIT_CODES_FAILURE,
} = ACTION_TYPES

export function clear() {
    return { type: CLEAR_COMMUNITY_FORM }
}

export function clearError() {
    return { type: CLEAR_COMMUNITY_FORM_ERROR }
}

export function clearFieldError(field) {
    return {
        type: CLEAR_COMMUNITY_FORM_FIELD_ERROR,
        payload: field
    }
}

export function changeTab(tab) {
    return {
        type: CHANGE_COMMUNITY_FORM_TAB,
        payload: tab
    }
}

export function changeField(field, value) {
    return dispatch => {
        return promise(
            dispatch({
                type: CHANGE_COMMUNITY_FORM_FIELD,
                payload: { field, value }
            })
        )
    }
}

export function clearCertificateFields() {
    return { type: CLEAR_CERTIFICATE_FORM_FIELDS }
}

export function changeFieldDeep(field, value) {
    return dispatch => {
        return promise(
            dispatch({
                type: CHANGE_COMMUNITY_FORM_FIELD_DEEP,
                payload: { field, value }
            })
        )
    }
}

export function addReferralEmail() {
    return dispatch => {
        return promise(
            dispatch({ type: ADD_COMMUNITY_FORM_REFERRAL_EMAILS_FIELD })
        )
    }
}

export function setInitialReferralEmails(emails) {
    return dispatch => {
        return promise(
            dispatch({
                type: SET_INITIAL_COMMUNITY_FORM_REFERRAL_EMAILS_FIELD,
                payload: emails
            })
        )
    }
}

export function changeMarketplaceField(field, value) {
    return dispatch => {
        return promise(
            dispatch({
                type: CHANGE_COMMUNITY_FORM_MARKET_PLACE_FIELD,
                payload: { field, value }
            })
        )
    }
}

export function changeFields(changes) {
    return dispatch => {
        return promise(
            dispatch({
                type: CHANGE_COMMUNITY_FORM_FIELDS,
                payload: changes
            })
        )
    }
}

function validate(data, validator, actionType, options) {
    return dispatch => {
        return validator.validate(data, options).then(() => {
            dispatch({ type: actionType, payload: { success: true } })
            return true
        }).catch(errors => {
            dispatch({ type: actionType, payload: { success: false, errors } })
            return false
        })
    }
}

export function validateLegalInfo(data, options) {
    return validate(data, legalInfoValidator, VALIDATE_COMMUNITY_FORM_LEGAL_INFO, options)
}

export function validateMarketplace(data) {
    return validate(data, marketplaceValidator, VALIDATE_COMMUNITY_FORM_MARKETPLACE)
}

export function validateAffiliateRelationship(data) {
    return validate(data, legalInfoValidator, VALIDATE_COMMUNITY_FORM_AFFILIATE_RELATIONSHIP)
}

export function validateUniq(orgId, data) {
    return dispatch => {
        dispatch({ type: VALIDATE_COMMUNITY_DATA_UNIQ_REQUEST })
        return service.validateUniq(orgId, data).then(({ data } = {}) => {
            dispatch({ type: VALIDATE_COMMUNITY_DATA_UNIQ_SUCCESS, payload: data })

            return allAreTrue(
                data.oid !== null ? data.oid : true,
                data.name !== null ? data.name : true
            )
        }).catch(e => {
            dispatch({ type: VALIDATE_COMMUNITY_DATA_UNIQ_FAILURE, payload: e })
        })
    }
}

export function validateBusinessUnitCodes(params) {
    return dispatch => {
        dispatch({ type: VALIDATE_COMMUNITY_BUSINESS_UNIT_CODES_REQUEST })

        return service.getNonUniqBusinessUnitCodes(params).then(({ data }) => {
            if (data?.length) {
                dispatch({
                    type: VALIDATE_COMMUNITY_BUSINESS_UNIT_CODES_SUCCESS,
                    payload: data
                })

                return false
            }

            return true
        }).catch(e => {
            dispatch({ type: VALIDATE_COMMUNITY_BUSINESS_UNIT_CODES_FAILURE, payload: e })
        })
    }
}

export function loadServerSelfSignedCertificate(params) {
    return dispatch => {
        dispatch({ type: LOAD_SERVER_SELF_TRUSTED_CERT_REQUEST })

        return service.loadServerSelfSignedCertificate(params).then(({ data = null, error }) => {
            dispatch({ type: LOAD_SERVER_SELF_TRUSTED_CERT_SUCCESS, payload: data })
        }).catch(e => {
            dispatch({ type: LOAD_SERVER_SELF_TRUSTED_CERT_FAILURE, payload: e })

            throw e
        })
    }
}

export function submit(data, params) {
    return dispatch => {
        dispatch({ type: SAVE_COMMUNITY_REQUEST })
        return service.save(data, params).then(response => {
            dispatch({ type: SAVE_COMMUNITY_SUCCESS, payload: response })
            return response
        }).catch(e => {
            dispatch({ type: SAVE_COMMUNITY_FAILURE, payload: e })
        })
    }
}

export function loadPictures({ pictureId, communityId, organizationId }) {
    return () => service.findPictureById(pictureId, { communityId, organizationId })
}
