import { integer, Shape } from './types'

const Scheme = Shape({
    organizationId: integer().required()
})

export default Scheme
