import { integer, Shape, string } from './types'

const ProspectActivationFormScheme = Shape({
    activationDate: integer().nullable().required(),
    comment: string()
})

export default ProspectActivationFormScheme