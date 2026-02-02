import { omit } from 'underscore'

import { interpolate } from 'lib/utils/Utils'
import { VALIDATION_ERROR_TEXTS } from 'lib/Constants'

import BaseFormValidator from './BaseFormValidator'

const {
    EMPTY_FIELD,
    LENGTH_MAXIMUM,
} = VALIDATION_ERROR_TEXTS

const CONSTRAINTS = {
    username: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        },
        length: {
            maximum: 256,
            message: interpolate(LENGTH_MAXIMUM, 256)
        }
    },
    password: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        },
        length: {
            maximum: 128,
            message: interpolate(LENGTH_MAXIMUM, 128)
        }
    }
}

class LoginFormValidator extends BaseFormValidator {
    validate(data, { excluded = [] } = {}) {
        return super.validate(
            data, omit(CONSTRAINTS, excluded), { fullMessages: false }
        )
    }
}

const validator = new LoginFormValidator()
export default validator