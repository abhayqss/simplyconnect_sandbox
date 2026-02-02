import { filesize } from 'filesize'

import BaseFormValidator from './BaseFormValidator'

import { interpolate } from 'lib/utils/Utils'

import {
    VALIDATION_ERROR_TEXTS,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'


const { GIF, JPG, JPEG, PNG, CERT, CER, CRT } = ALLOWED_FILE_FORMAT_MIME_TYPES

const ALLOWED_IMAGE_FILE_TYPES = [GIF, JPG, JPEG, PNG]
const ALLOWED_CERTIFICATE_FILE_TYPES = [CERT, CER, CRT]

const ALLOWED_LOGO_FILE_SIZE_IN_MB = 1
const ALLOWED_PICTURE_FILE_SIZE_IN_MB = 20
const ALLOWED_CERtIFICATE_FILE_SIZE_IN_MB = 50

const calculateFileSize = value => filesize(value, { output: 'object', exponent: 2 }).value

const {
    FILE_SIZE,
    FILE_FORMAT,
    EMPTY_FIELD,
    EMAIL_FORMAT,
    PHONE_FORMAT,
    NUMBER_FORMAT,
    LENGTH_MINIMUM,
    LENGTH_MAXIMUM,
    NUMBER_FORMAT_SPECIFIC
} = VALIDATION_ERROR_TEXTS

const CONSTRAINTS = {
    name: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        },
        length: {
            minimum: 3,
            maximum: 256,
            tooShort: interpolate(LENGTH_MINIMUM, 3),
            tooLong: interpolate(LENGTH_MAXIMUM, 256)
        }
    },
    oid: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        },
        length: {
            maximum: 256,
            message: interpolate(LENGTH_MAXIMUM, 256)
        }
    },
    /*isSharingData: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        },
    },*/
    numberOfBeds: {
        length: {
            maximum: 256,
            tooLong: interpolate(LENGTH_MAXIMUM, 256)
        }
    },
    numberOfVacantBeds: {
        length: {
            maximum: 256,
            tooLong: interpolate(LENGTH_MAXIMUM, 256)
        }
    },
    email: {
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
    },
    phone: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        },
        format: {
            pattern: /\+?\d{10,16}/,
            message: PHONE_FORMAT
        }
    },
    logo: (value) => {
        let constraint = {}

        if (value instanceof File) {
            if (ALLOWED_LOGO_FILE_SIZE_IN_MB < calculateFileSize(value.size)) {
                constraint.numericality = {
                    notValid: interpolate(FILE_SIZE, ALLOWED_LOGO_FILE_SIZE_IN_MB)
                }
            }

            if (!ALLOWED_IMAGE_FILE_TYPES.includes(value.type)) {
                constraint.inclusion = {
                    message: interpolate(FILE_FORMAT, value.type)
                }
            }
        }

        return constraint
    },
    pictureFiles: {
        array: {
            size: {
                comparison: {
                    a: value => calculateFileSize(value),
                    b: ALLOWED_PICTURE_FILE_SIZE_IN_MB,
                    compare: (a, b) => a <= b,
                    message: interpolate(FILE_SIZE, ALLOWED_PICTURE_FILE_SIZE_IN_MB)
                }
            },
            type: {
                inclusion: value => {
                    if (!ALLOWED_IMAGE_FILE_TYPES.includes(value)) {
                        return {
                            message: interpolate(FILE_FORMAT, value)
                        }
                    } else {
                        return false
                    }
                }
            }
        },
    },
    street: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        },
        length: {
            minimum: 3,
            maximum: 256,
            tooShort: interpolate(LENGTH_MINIMUM, 3),
            tooLong: interpolate(LENGTH_MAXIMUM, 256)
        }
    },
    city: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        },
        length: {
            maximum: 256,
            message: interpolate(LENGTH_MAXIMUM, 256)
        }
    },
    stateId: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        }
    },
    zipCode: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        },
        length: {
            is: 5,
            message: interpolate(NUMBER_FORMAT_SPECIFIC, 5)
        },
        numericality: {
            notValid: NUMBER_FORMAT
        }
    },
    'docutrackPharmacyConfig.serverDomain': function(value, attributes, attributeName, { included }) {
        return included.shouldValidateDocutrack 
            ? { 
                presence: {
                    allowEmpty: false,
                    message: EMPTY_FIELD
                }
            }
            : null
    },
    'docutrackPharmacyConfig.clientType': function (value, attributes, attributeName, { included }) {
        return included.shouldValidateDocutrack 
            ? { 
                presence: {
                    allowEmpty: false,
                    message: EMPTY_FIELD
                }
            }
            : null
    },
    'docutrackPharmacyConfig.publicKeyCertificates': function(value, attributes, attributeName, { included }) {
        if (!included.shouldValidateDocutrack || included.useSuggestedCertificate) {
            return null
        }

        if (!value.length) {
            return {
                presence: {
                    allowEmpty: false,
                    message: EMPTY_FIELD
                }
            }
        }

        return {
            array: {
                size: {
                    comparison: {
                        a: value => calculateFileSize(value),
                        b: ALLOWED_CERtIFICATE_FILE_SIZE_IN_MB,
                        compare: (a, b) => a <= b,
                        message: interpolate(FILE_SIZE, ALLOWED_CERtIFICATE_FILE_SIZE_IN_MB)
                    }
                },
                type: {
                    inclusion: value => {
                        if (!ALLOWED_CERTIFICATE_FILE_TYPES.includes(value)) {
                            return {
                                message: interpolate(FILE_FORMAT, value)
                            }
                        } else {
                            return false
                        }
                    }
                }
            },
        }
    },
    'docutrackPharmacyConfig.businessUnitCodes': function(value, attributes, attributeName, { included }) {
        if (!included.shouldValidateDocutrack) {
            return null
        }

        return {
            array: {
                exclusion: function(item) {
                    return value.filter(o => o === item).length > 1
                        ? {
                            within: value,
                            message: 'Business unit code already exists. Please enter a unique code.'
                        }
                        : null
                }
            }
        }
    },
}

class CommunityFormLegalInfoValidator extends BaseFormValidator {
    validate (data, options) {
        return super.validate(data, CONSTRAINTS, { fullMessages: false, ...options })
    }
}

const validator = new CommunityFormLegalInfoValidator()
export default validator