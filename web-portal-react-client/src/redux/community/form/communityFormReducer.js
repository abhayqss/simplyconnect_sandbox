import {
    ACTION_TYPES,
    VALIDATION_ERROR_TEXTS
} from 'lib/Constants'

import {
    anyIsFalse,
    interpolate
} from 'lib/utils/Utils'

import { updateFieldErrors } from '../../utils/Form'

import InitialState, { ReferralEmail } from './CommunityFormInitialState'

const {
    LOGOUT_SUCCESS,
    CLEAR_ALL_AUTH_DATA,
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
    VALIDATE_COMMUNITY_FORM_MARKETPLACE,
    VALIDATE_COMMUNITY_FORM_AFFILIATE_RELATIONSHIP,

    VALIDATE_COMMUNITY_DATA_UNIQ_SUCCESS,

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

const {
    NON_UNIQ,
    NON_UNIQ_SHORT
} = VALIDATION_ERROR_TEXTS

const initialState = new InitialState()

export default function communityFormReducer (state = initialState, action) {
    if (!(state instanceof InitialState)) {
        return initialState.mergeDeep(state)
    }

    switch (action.type) {
        case LOGOUT_SUCCESS:
        case CLEAR_ALL_AUTH_DATA:
        case CLEAR_COMMUNITY_FORM:
            return state.clear()

        case CLEAR_COMMUNITY_FORM_ERROR:
            return state.removeIn(['error'])

        case CLEAR_COMMUNITY_FORM_FIELD_ERROR: {
            const field = action.payload
            return state.setIn(['fields', field + 'HasError'], false)
                .setIn(['fields', field + 'ErrorMsg'], '')
        }

        case CHANGE_COMMUNITY_FORM_TAB:
            return state.setIn(['tab'], action.payload)

        case CHANGE_COMMUNITY_FORM_FIELD: {
            const { field, value } = action.payload
            return state.setIn(['fields', ...field.split('.')], value)
        }

        case CHANGE_COMMUNITY_FORM_FIELD_DEEP: {
            const { field, value } = action.payload
            return state.setIn(['fields', ...field.split('.')], value)
        }

        case CHANGE_COMMUNITY_FORM_MARKET_PLACE_FIELD: {
            const { field, value } = action.payload
            return state.setIn(['fields', 'marketplace', field], value)
        }

        case ADD_COMMUNITY_FORM_REFERRAL_EMAILS_FIELD: {
            const value = [...state.fields.marketplace.referralEmails, ReferralEmail()]

            return state.setIn(['fields', 'marketplace', 'referralEmails'], value)
        }

        case SET_INITIAL_COMMUNITY_FORM_REFERRAL_EMAILS_FIELD: {
            let emails = action.payload

            let value = emails.map(email => (
                ReferralEmail({ canEdit: false, value: email })
            ))

            return state.setIn(['fields', 'marketplace', 'referralEmails'], value)
        }

        case CHANGE_COMMUNITY_FORM_FIELDS:
            return state.mergeDeep({ fields: action.payload })


        case VALIDATE_COMMUNITY_DATA_UNIQ_SUCCESS: {
            const {
                oid,
                name
            } = action.payload

            if (anyIsFalse(oid, name)) {
                const errorText = (s, v) => interpolate(s, 'community', v)

                return state
                    .setIn(['isValid'], false)
                    .setIn(['isValidLegalInfoTab'], false)
                    .mergeIn(['fields'], {
                            ...oid === false && {
                                oidHasError: true,
                                oidErrorText: 'Entered OID already exists in the system.'
                            },
                            ...name === false && {
                                nameHasError: true,
                                nameErrorText: errorText(NON_UNIQ_SHORT, 'name')
                            }
                        }
                    )
            }

            break
        }

        case VALIDATE_COMMUNITY_FORM_LEGAL_INFO: {
            const path = ['fields']
            const { success, errors } = action.payload

            return state
                .setIn(['isValid'], success)
                .setIn(['isValidLegalInfoTab'], success)
                .setIn(path, updateFieldErrors(state.getIn(path), errors))
        }

        case VALIDATE_COMMUNITY_FORM_MARKETPLACE: {
            const path = ['fields', 'marketplace']
            const { success, errors } = action.payload

            return state
                .setIn(['isValid'], success)
                .setIn(['isValidMarketplaceTab'], success)
                .setIn(path, updateFieldErrors(state.getIn(path), errors))
        }

        case SAVE_COMMUNITY_REQUEST: {
            return state.setIn(['isFetching'], true)
        }

        case SAVE_COMMUNITY_SUCCESS: {
            return state.setIn(['isFetching'], false)
        }

        case LOAD_SERVER_SELF_TRUSTED_CERT_REQUEST: {
            return state.setIn(['isFetching'], true)
        }

        case LOAD_SERVER_SELF_TRUSTED_CERT_SUCCESS: {
            return state.setIn(
                ['fields', 'docutrackPharmacyConfig', 'serverCertificate'],
                action.payload
            ).setIn(['isFetching'], false)
        }

        case SAVE_COMMUNITY_FAILURE:
        case LOAD_SERVER_SELF_TRUSTED_CERT_FAILURE:
        case VALIDATE_COMMUNITY_BUSINESS_UNIT_CODES_FAILURE: {
            return state.setIn(['error'], action.payload)
                .setIn(['isFetching'], false)
        }

        case CLEAR_CERTIFICATE_FORM_FIELDS:
            return state
                .setIn(['fields', 'docutrackPharmacyConfig', 'serverCertificate'], undefined)
                .setIn(['fields', 'docutrackPharmacyConfig', 'configuredCertificate'], undefined)
                .setIn(['fields', 'docutrackPharmacyConfig', 'useSuggestedCertificate'], true)
                .setIn(['fields', 'docutrackPharmacyConfig', 'publicKeyCertificates'], [])
                .setIn(['fields', 'docutrackPharmacyConfig', 'publicKeyCertificatesHasError'], false)
                .setIn(['fields', 'docutrackPharmacyConfig', 'publicKeyCertificatesErrorText'], '')

        case VALIDATE_COMMUNITY_BUSINESS_UNIT_CODES_SUCCESS: {
            const codes = action.payload

            const TEXT = {
                CODE: `code${codes.length > 1 ? 's' : ''}`,
                EXIST: `exist${codes.length > 1 ? '' : 's'}`
            }

            return state
                .setIn(['error'], Error(
                    `Business unit ${TEXT.CODE} ${codes.join(', ')} already ${TEXT.EXIST}. Please enter a unique code.`
                ))
        }
    }

    return state
}
