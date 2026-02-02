import { Shape, string } from './types'

const OrganizationFeaturesScheme = Shape({
    name: string().required(),
    color: string().nullable().required(),
})

export default OrganizationFeaturesScheme
