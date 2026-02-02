import { useQuery } from 'hooks/common'

import service from 'services/ClientService'

const fetch = params => service.findPrimaryContacts(params)

function usePrimaryContactsQuery(params, options) {
    return useQuery('ClientPrimaryContacts', params, {
        fetch,
        ...options
    })
}

export default usePrimaryContactsQuery
