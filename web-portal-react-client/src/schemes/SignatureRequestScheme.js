import { VALIDATION_ERROR_TEXTS } from 'lib/Constants'

import {
    Shape,
    ListOf,
    string,
    integer,    
    phoneNumber
} from './types'

import EmailScheme from './EmailScheme'

const { EMPTY_FIELD } = VALIDATION_ERROR_TEXTS

const SignatureRequestScheme = Shape({
    templateIds: ListOf(integer()).min(1, EMPTY_FIELD),
    recipientId: integer().required(),
    recipientType: string().required(),
    expirationDate: integer().nullable().required(),
    notificationMethod: string().nullable().when(
        'recipientType', (recipientType, scheme) => (
            recipientType !== 'SELF' ? scheme.required() : scheme
        )
    ),
    phone: phoneNumber().when(
        ['$included', 'notificationMethod'],
        (included, notificationMethod, scheme) => {
            const { hasLinkedAccount, isClientSelected } = included

            return notificationMethod === 'SMS'
            || (
                notificationMethod === 'EMAIL'
                && isClientSelected
                && !hasLinkedAccount
            ) ? scheme.required() : scheme
        }
    ),
    email: EmailScheme.when(
        'notificationMethod', (notificationMethod, scheme) => (
            notificationMethod === 'EMAIL' ? scheme.required() : scheme
        )
    )
})

export default SignatureRequestScheme
