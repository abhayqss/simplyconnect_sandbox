import { interpolate } from 'lib/utils/Utils'
import { VALIDATION_ERROR_TEXTS } from 'lib/Constants'

import BaseFormValidator from './BaseFormValidator'

const {
    EMPTY_FIELD,
    PHONE_FORMAT,
    EMAIL_FORMAT,
    NUMBER_FORMAT,
    LENGTH_EQUAL,
    LENGTH_MINIMUM,
    LENGTH_MAXIMUM
} = VALIDATION_ERROR_TEXTS

const NOT_EMPTY_CONSTRAINT = {
    presence: {
        allowEmpty: false,
        message: EMPTY_FIELD
    }
}

const PHONE_CONSTRAINT = {
    format: {
        pattern: /(\+?\d{10,16})?/,
        message: PHONE_FORMAT
    }
}

const EMAIL_CONSTRAINT = {
    email: {
        message: EMAIL_FORMAT
    }
}

const CONSTRAINTS = {
    "essentials.authorRole": {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        },
    },
    "essentials.date": {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        }
    },
    "essentials.typeId": {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        }
    },
    "description.location": {
        length: {
            maximum: 5000,
            message: interpolate(LENGTH_MAXIMUM, 5000)
        }
    },
    "description.situation": {
        length: {
            maximum: 5000,
            message: interpolate(LENGTH_MAXIMUM, 5000)
        }
    },
    "description.background": {
        length: {
            maximum: 5000,
            message: interpolate(LENGTH_MAXIMUM, 5000)
        }
    },
    "description.assessment": {
        length: {
            maximum: 5000,
            message: interpolate(LENGTH_MAXIMUM, 5000)
        }
    },
    "description.isFollowUpExpected": {
        presence: true
    },
    "description.followUpDetails": (value, attributes) => (
        attributes.description.isFollowUpExpected ? {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            },
            length: {
                maximum: 5000,
                message: interpolate(LENGTH_MAXIMUM, 5000)
            }
        } : null
    ),
    "treatment.hasPhysician": {
        presence: true
    },
    "treatment.physician.firstName": (value, attributes) => (
        attributes.treatment.hasPhysician ? {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            },
            length: {
                minimum: 2,
                maximum: 256,
                tooShort: interpolate(LENGTH_MINIMUM, 2),
                tooLong: interpolate(LENGTH_MAXIMUM, 256)
            }
        } : null
    ),
    "treatment.physician.lastName": (value, attributes) => (
        attributes.treatment.hasPhysician ? {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            },
            length: {
                minimum: 2,
                maximum: 256,
                tooShort: interpolate(LENGTH_MINIMUM, 2),
                tooLong: interpolate(LENGTH_MAXIMUM, 256)
            }
        } : null
    ),
    "treatment.physician.phone": (value, attributes) => (
        attributes.treatment.hasPhysician ? PHONE_CONSTRAINT : null
    ),
    "treatment.physician.hasAddress": (value, attributes) => (
        attributes.treatment.hasPhysician ? {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            },
        } : null
    ),
    "treatment.physician.address.street": (value, attributes) => (
        attributes.treatment.physician.hasAddress ? {
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
        } : null
    ),
    "treatment.physician.address.city": (value, attributes) => (
        attributes.treatment.physician.hasAddress ? {
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
        } : null
    ),
    "treatment.physician.address.stateId": (value, attributes) => (
        attributes.treatment.physician.hasAddress ? NOT_EMPTY_CONSTRAINT : null
    ),
    "treatment.physician.address.zip": (value, attributes) => (
        attributes.treatment.physician.hasAddress ? {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            },
            length: {
                is: 5,
                message: interpolate(LENGTH_EQUAL, 5)
            },
            numericality: {
                notValid: NUMBER_FORMAT
            }
        } : null
    ),
    "treatment.hasHospital": {
        presence: true
    },
    "treatment.hospital.name": (value, attributes) => (
        attributes.treatment.hasHospital ? {
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
        } : null
    ),
    "treatment.hospital.phone": (value, attributes) => (
        attributes.treatment.hasHospital ? PHONE_CONSTRAINT : null
    ),
    "treatment.hospital.hasAddress": (value, attributes) => (
        attributes.treatment.hasHospital ? {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            },
        } : null
    ),
    "treatment.hospital.address.street": (value, attributes) => (
        attributes.treatment.hospital.hasAddress ? {
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
        } : null
    ),
    "treatment.hospital.address.city": (value, attributes) => (
        attributes.treatment.hospital.hasAddress ? {
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
        } : null
    ),
    "treatment.hospital.address.stateId": (value, attributes) => (
        attributes.treatment.hospital.hasAddress ? NOT_EMPTY_CONSTRAINT : null
    ),
    "treatment.hospital.address.zip": (value, attributes) => (
        attributes.treatment.hospital.hasAddress ? {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            },
            length: {
                is: 5,
                message: interpolate(LENGTH_EQUAL, 5)
            },
            numericality: {
                notValid: NUMBER_FORMAT
            }
        } : null
    ),
    hasResponsibleManager: {
        presence: true
    },
    "responsibleManager.firstName": (value, attributes) => (
        attributes.hasResponsibleManager ? {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            },
            length: {
                minimum: 2,
                maximum: 256,
                tooShort: interpolate(LENGTH_MINIMUM, 2),
                tooLong: interpolate(LENGTH_MAXIMUM, 256)
            }
        } : null
    ),
    "responsibleManager.lastName": (value, attributes) => (
        attributes.hasResponsibleManager ? {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            },
            length: {
                minimum: 2,
                maximum: 256,
                tooShort: interpolate(LENGTH_MINIMUM, 2),
                tooLong: interpolate(LENGTH_MAXIMUM, 256)
            }
        } : null
    ),
    "responsibleManager.phone": (value, attributes) => (
        attributes.hasResponsibleManager ? PHONE_CONSTRAINT : null
    ),
    "responsibleManager.email": (value, attributes) => (
        attributes.hasResponsibleManager ? EMAIL_CONSTRAINT : null
    ),
    hasRegisteredNurse: {
        presence: true
    },
    "registeredNurse.firstName": (value, attributes) => (
        attributes.hasRegisteredNurse ? {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            },
            length: {
                minimum: 2,
                maximum: 256,
                tooShort: interpolate(LENGTH_MINIMUM, 2),
                tooLong: interpolate(LENGTH_MAXIMUM, 256)
            }
        } : null
    ),
    "registeredNurse.lastName": (value, attributes) => (
        attributes.hasRegisteredNurse ? {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            },
            length: {
                minimum: 2,
                maximum: 256,
                tooShort: interpolate(LENGTH_MINIMUM, 2),
                tooLong: interpolate(LENGTH_MAXIMUM, 256)
            }
        } : null
    ),
    "registeredNurse.hasAddress": (value, attributes) => (
        attributes.hasRegisteredNurse ? {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            },
        } : null
    ),
    "registeredNurse.address.street": (value, attributes) => (
        attributes.registeredNurse.hasAddress ? {
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
        } : null
    ),
    "registeredNurse.address.city": (value, attributes) => (
        attributes.registeredNurse.hasAddress ? {
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
        } : null
    ),
    "registeredNurse.address.stateId": (value, attributes) => (
        attributes.registeredNurse.hasAddress ? NOT_EMPTY_CONSTRAINT : null
    ),
    "registeredNurse.address.zip": (value, attributes) => (
        attributes.registeredNurse.hasAddress ? {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            },
            length: {
                is: 5,
                message: interpolate(LENGTH_EQUAL, 5)
            },
            numericality: {
                notValid: NUMBER_FORMAT
            }
        } : null)
}

class EventFormValidator extends BaseFormValidator {
    validate(data) {
        return super.validate(data, CONSTRAINTS, { fullMessages: false })
    }
}

export default new EventFormValidator()