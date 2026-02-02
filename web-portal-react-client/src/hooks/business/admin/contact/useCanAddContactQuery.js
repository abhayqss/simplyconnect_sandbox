import { useQuery } from '@tanstack/react-query'

import service from 'services/ContactService'

const fetch = params => service.canAdd(
    params, { response: { extractDataOnly: true } }
)

export default function useCanAddContactQuery(params, options) {
    return useQuery(['Contact.CanAdd', params], () => fetch(params), options)
}