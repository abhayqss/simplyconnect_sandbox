import validate from 'validate.js'

import {
    VALIDATION_ERROR_TEXTS
} from 'lib/Constants'

import { integer, Shape, string, phoneNumber } from './types'

const {
    EMAIL_FORMAT
} = VALIDATION_ERROR_TEXTS

const { PATTERN: EMAIL_PATTERN } = validate.validators.email

const InquiryFormScheme = Shape({
    id: integer().nullable(),
    date: integer().nullable(),
    notes: string().nullable(),
	service: string().nullable(),
    email: string().nullable().matches(EMAIL_PATTERN, {
        message: EMAIL_FORMAT,
        excludeEmptyString: true,
    }).when(
        ['$included'],
        (included, scheme) => (
            included.phone ? scheme : scheme.required()
        )
    ),
    status: string().nullable(),
	lastName: string().nullable().required(),
	firstName: string().nullable().required(),
    phone: phoneNumber().nullable().when(
        ['$included'],
        (included, scheme) => (
            included.email ? scheme : scheme.required()
        )
    ),
    referringCommunity: integer().nullable()
})

export default InquiryFormScheme