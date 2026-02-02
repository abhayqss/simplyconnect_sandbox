import {
    Shape,
    integer
} from './types'

const ProspectEventFilterScheme = Shape({
    fromDate: integer().nullable().required(),
    toDate: integer().nullable().required(),
})

export default ProspectEventFilterScheme
