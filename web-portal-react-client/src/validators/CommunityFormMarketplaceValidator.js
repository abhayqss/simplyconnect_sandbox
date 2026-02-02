import BaseFormValidator from "./BaseFormValidator";

import { interpolate } from 'lib/utils/Utils'
import { VALIDATION_ERROR_TEXTS } from 'lib/Constants'

const {
    EMPTY_FIELD,
    EMAIL_FORMAT,
    LENGTH_MAXIMUM,
} = VALIDATION_ERROR_TEXTS

const CONSTRAINTS = {
    primaryFocusIds: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        }
    },
    communityTypeIds: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        }
    },
    serviceTreatmentApproachIds: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        }
    },
    servicesSummaryDescription: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        },
        length: {
            maximum: 20000,
            message: interpolate(LENGTH_MAXIMUM, 20000)
        }
    },
    prerequisite: {
        length: {
            maximum: 5000,
            message: interpolate(LENGTH_MAXIMUM, 5000)
        }
    },
    exclusion: {
        length: {
            maximum: 5000,
            message: interpolate(LENGTH_MAXIMUM, 5000)
        }
    },
    allowAppointments: {
        presence: true
    },
    appointmentsSecureEmail: (value, attributes) => {
        let constrains = {}

        if (value) {
            constrains = {
                presence: {
                    allowEmpty: false,
                    message: EMPTY_FIELD
                },
                email: {
                    message: EMAIL_FORMAT
                },
                length: {
                    maximum: 256,
                    message: interpolate(LENGTH_MAXIMUM, 256)
                }
            }
        }

        if (attributes.allowAppointments) {
            constrains.presence = {
                message: EMPTY_FIELD,
                allowEmpty: false
            }
        }
        return constrains
    },
    referralEmails: {
        array: {
            value(value, attributes, attributeName, { index, context }) {
                let referralEmails = context.referralEmails
                    .filter(o => o !== attributes)
                    .map(o => o.value)

                const constraints = {
                    presence: {
                        allowEmpty: index > 0,
                        message: EMPTY_FIELD
                    },
                    exclusion: {
                        within: referralEmails,
                        message: 'Email already entered. Please type in a unique email.'
                    }
                }

                if (attributes.canEdit) {
                    constraints.email = {
                        message: EMAIL_FORMAT
                    }
                }

                return constraints
            }
        }
    }
}

class CommunityFormMarketplaceValidator extends BaseFormValidator {
    validate(data) {
        return super.validate(data, CONSTRAINTS, { fullMessages: false })
    }
}

const validator = new CommunityFormMarketplaceValidator()
export default validator