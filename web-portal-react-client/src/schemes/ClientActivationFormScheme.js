import { integer, Shape, string } from './types'

const ClientActivationFormScheme = Shape({
    intakeDate: integer().nullable().required(),
    programType: string().nullable(),
    comment: string()
})

export default ClientActivationFormScheme