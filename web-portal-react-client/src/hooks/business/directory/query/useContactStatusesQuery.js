import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = () => service.findContactStatuses(
    { response: { extractDataOnly: true } }
)

export default function useContactStatusesQuery(params, options) {
    return useQuery(['Directory.ContactStatuses', params], () => fetch(params), options)
}
