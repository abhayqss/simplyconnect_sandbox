import { useQuery } from 'hooks/common'

import service from 'services/DirectoryService'

const fetch = () => service.findSupportTicketTypes()

function useSupportTicketTypes(params, options) {
    return useQuery('SupportTicketTypes', params, {
        fetch,
        ...options,
    })
}

export default useSupportTicketTypes
