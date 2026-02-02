import { string } from './types'

export default (asyncValidate) => string().test({
    name: 'requisitionNumber',
    test: asyncValidate,
    message: 'Requisition or accession number should be unique.',
    exclusive: true,
})