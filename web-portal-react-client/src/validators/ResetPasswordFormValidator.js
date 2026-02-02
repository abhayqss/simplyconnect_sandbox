import { omit } from 'underscore'

import { interpolate } from 'lib/utils/Utils'
import { VALIDATION_ERROR_TEXTS } from 'lib/Constants'

import BaseFormValidator from './BaseFormValidator'

const {
    EMPTY_FIELD,
    EMAIL_FORMAT,
    LENGTH_MAXIMUM,
} = VALIDATION_ERROR_TEXTS

const CONSTRAINTS = {
    companyId: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
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
}

class ResetPasswordFormValidator extends BaseFormValidator {
    validate(data, { excluded = [] } = {}) {
        return super.validate(
            data, omit(CONSTRAINTS, excluded), { fullMessages: false }
        )
    }
}

const validator = new ResetPasswordFormValidator()
export default validator