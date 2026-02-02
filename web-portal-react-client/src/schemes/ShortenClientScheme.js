import { string, Shape } from './types'

const ClientScheme = Shape({
    lastName: string().nullable().required(),
    firstName: string().nullable().required(),
    birthDate: string().nullable().required(),
})

export default ClientScheme