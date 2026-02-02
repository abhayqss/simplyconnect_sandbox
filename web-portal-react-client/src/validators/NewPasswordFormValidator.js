import { omit } from 'underscore'

import {
    interpolate,
    getPasswordRegExp
} from 'lib/utils/Utils'

import { VALIDATION_ERROR_TEXTS } from 'lib/Constants'

import BaseFormValidator from './BaseFormValidator'

const {
    EMPTY_FIELD,
    LENGTH_MAXIMUM,
    PASSWORD_COMPLEXITY,
    CONFIRM_PASSWORD_MATCH
} = VALIDATION_ERROR_TEXTS

const CONSTRAINTS = {
    firstName: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        },
        length: {
            maximum: 256,
            message: interpolate(LENGTH_MAXIMUM, 256)
        }
    },
    lastName: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        },
        length: {
            maximum: 256,
            message: interpolate(LENGTH_MAXIMUM, 256)
        }
    },
    password: (value, attributes, attributeName, options) => {
        const constraints = {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            }
        }

        return getPasswordRegExp(options).test(value)
            ? constraints
            : {
                ...constraints,
                inclusion: {
                    message: PASSWORD_COMPLEXITY
                }
            }
    },
    confirmPassword: (value, attributes) => {
        const constraints = {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            }
        }

        return (value < attributes.password || value > attributes.password)
            ? {
                ...constraints,
                inclusion: {
                    message: CONFIRM_PASSWORD_MATCH
                }
            }
            : constraints
    }
}

class NewPasswordFormValidator extends BaseFormValidator {
    validate(data, { excluded = [], ...options } = {}) {
        return super.validate(
            data,
            omit(CONSTRAINTS, excluded),
            { fullMessages: false, ...options }
        )
    }
}

const validator = new NewPasswordFormValidator()
export default validator