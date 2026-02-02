import { number, object, string as yupString, array, boolean } from 'yup'

import { VALIDATION_ERROR_TEXTS } from 'lib/Constants'

const { isCollection } = require('immutable')

const phoneRegexp = /(^$)|^(\+?\d{10,16})$/
const { PHONE_FORMAT } = VALIDATION_ERROR_TEXTS

export const integer = message => number().integer(message).nullable()

export const string = message => yupString(message)

export const stringMax = (limit, message) => yupString(message).max(limit)

export const stringMin = (limit, message) => yupString(message).min(limit)

export const Shape = (o, deps) => object().shape(o, deps)

export const bool = message => boolean(message).nullable()

export function ListOf(scheme) {
    return (scheme != null ? array().of(scheme) : array())
        .transform((_, value) => isCollection(value) ? value.toArray() : value)
}

export const phoneNumber = (message = PHONE_FORMAT) => yupString().nullable().matches(phoneRegexp, {
    message,
    excludeEmptyString: true
})

export const date = message => integer(message).transform(value => (
    value ? value : null
))
