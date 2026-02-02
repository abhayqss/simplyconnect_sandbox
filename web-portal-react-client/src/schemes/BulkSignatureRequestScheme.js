import { VALIDATION_ERROR_TEXTS } from 'lib/Constants'

import {
  Shape,
  ListOf,
  integer, bool
} from './types'

const CLIENTS_WITHOUT_PRIMARY_CONTACT_ERROR_MESSAGE = "To send a signature request, please add or select a primary contact on the Edit Client Screen"

const { EMPTY_FIELD } = VALIDATION_ERROR_TEXTS

const Community = Shape({
    communityId: integer().required(),
    clientIds: ListOf(integer()).min(1, EMPTY_FIELD)
        .when(
            ['$included'],
            (included, scheme) => {
                return scheme.noIntersectionWith(
                    included.clientsWithoutPrimaryContact,
                    CLIENTS_WITHOUT_PRIMARY_CONTACT_ERROR_MESSAGE
                )
            }
        )
})

const BulkSignatureRequestScheme = Shape({
    templateIds: ListOf(integer()).min(1, EMPTY_FIELD),
    communities: ListOf(Community).min(1, EMPTY_FIELD),
    organizationId: integer().required(),
    whetherMultiplePeopleNeedToSign: bool().required(),
    expirationDate: integer().nullable().required(),
})

export default BulkSignatureRequestScheme
