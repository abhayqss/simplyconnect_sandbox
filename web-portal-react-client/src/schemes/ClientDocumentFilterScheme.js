import {
    Shape,
    integer
} from './types'

const ClientDocumentFilterScheme = Shape({
    fromDate: integer().required(),
    toDate: integer().required(),
})

export default ClientDocumentFilterScheme
