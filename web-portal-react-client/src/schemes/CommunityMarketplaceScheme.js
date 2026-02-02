import {
    filter,
    indexOf
} from 'underscore'

import { array, boolean } from 'yup'

import { Shape, string, ListOf } from './types'

import { VALIDATION_ERROR_TEXTS } from 'lib/Constants'

import EmailScheme from './EmailScheme'

const { EMPTY_FIELD } = VALIDATION_ERROR_TEXTS

const ReferralEmail = Shape({
    value: string().nullable().when(
        ['canEdit', '$included'],
        (canEdit, included, schema, { value }) => {
            const i = indexOf(included.referralEmails, value)

            const sch = (
                canEdit ? EmailScheme : schema
            ).test(
                'is-uniq-referral-email-if-not-empty',
                'Email already entered. Please type in a unique email.',
                value => {
                    const count = filter(
                        included.referralEmails,
                        email => email === value
                    )?.length

                    return !(value && count > 1)
                }
            )

            return +i === 0 ? sch.required() : sch
        }
    ),
    canEdit: boolean()
})

const CommunityMarketplaceScheme = Shape({
    marketplace: Shape({
        serviceCategoryIds: ListOf().min(1, EMPTY_FIELD),
        serviceIds: ListOf().min(1, EMPTY_FIELD),
        servicesSummaryDescription: string().required().max(5000),
        referralEmails: array().when(['$included'], (included, shema) => {
            return included.shouldValidateReferralEmails ? ListOf(ReferralEmail).min(1, EMPTY_FIELD) : shema
        })
    })
})

export default CommunityMarketplaceScheme
