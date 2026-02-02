import { useQuery } from '@tanstack/react-query'

import service from 'services/ContactService'

const fetch = ({ contactId }) => service.findById(
    contactId, { response: { extractDataOnly: true } }
)

export default function useContactQuery(params, options) {
    return useQuery(['Contact', params], () => fetch(params), options)
}