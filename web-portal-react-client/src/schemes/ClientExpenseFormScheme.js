import { integer, Shape, string } from './types'

const ClientActivationFormScheme = Shape({
    date: integer().nullable().required(),
    typeName: string().nullable().required(),
    cost: string().nullable().required(),
    comment: string().nullable()
})

export default ClientActivationFormScheme