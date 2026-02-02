import validate from 'validate.js'

import { string } from './types'

import { VALIDATION_ERROR_TEXTS } from 'lib/Constants'

const { EMAIL_FORMAT } = VALIDATION_ERROR_TEXTS
const { PATTERN: EMAIL_PATTERN } = validate.validators.email
 
const EmailScheme = string().nullable().matches(EMAIL_PATTERN, {
    message: EMAIL_FORMAT,
    excludeEmptyString: true,
})

export default EmailScheme
