import { omit } from 'underscore'

import { interpolate } from 'lib/utils/Utils'

import BaseFormValidator from './BaseFormValidator'

const ERROR_TEXT = 'Please type in the $0'

const CONSTRAINTS = {
    fromDate: {
        presence: {
            allowEmpty: false,
            message: interpolate(ERROR_TEXT, 'Date from')
        }
    },
    toDate: {
        presence: {
            allowEmpty: false,
            message: interpolate(ERROR_TEXT, 'Date to')
        }
    },
}

class ReportFilterValidator extends BaseFormValidator {
    validate(data, { excluded = [] } = {}) {
        return super.validate(
            data,
            omit(CONSTRAINTS, excluded),
            { fullMessages: false }
        )
    }
}

export default new ReportFilterValidator()