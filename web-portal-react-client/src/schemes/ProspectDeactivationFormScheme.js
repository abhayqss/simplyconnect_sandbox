import { integer, Shape, string } from './types'

const ProspectDeactivationFormScheme = Shape({
    deactivationDate: integer().nullable().required(),
    deactivationReason: string().nullable().required(),
    comment: string().nullable()
})

export default ProspectDeactivationFormScheme