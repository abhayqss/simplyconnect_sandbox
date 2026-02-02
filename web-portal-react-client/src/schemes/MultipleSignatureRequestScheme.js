import {VALIDATION_ERROR_TEXTS} from 'lib/Constants'

import {
  Shape,
  ListOf,
  integer, bool, string
} from './types'


const {EMPTY_FIELD} = VALIDATION_ERROR_TEXTS

const Community = Shape({
  communityId: integer().required(),
})
const submitterEmail = Shape({
  email: string().required(),
})
const signOrderEmail = Shape({
  email: string().required(),
  poaEmail: string(),
  usePOA: bool()
})

const BulkSignatureRequestScheme = Shape({
    templateIds: integer().nullable().required(),
    communities: ListOf(Community).min(1, EMPTY_FIELD).required(),
    organizationId: integer().required(),
    clientIds: ListOf(integer()).min(1, EMPTY_FIELD).required().nullable(),
    submitters:ListOf(submitterEmail).required().nullable(),
    signOrderEmails:ListOf(signOrderEmail).nullable(),
})

export default BulkSignatureRequestScheme
