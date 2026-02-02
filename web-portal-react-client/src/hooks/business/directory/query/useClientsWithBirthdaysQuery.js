import { useQuery } from 'hooks/common'

import service from 'services/DirectoryService'

const fetch = params => service.findClientsWithBirthdays(params)

function useClientsWithBirthdaysQuery(params, options) {
    return useQuery('ClientsWithBirthdays', params, {
        fetch,
        ...options,
    })
}

export default useClientsWithBirthdaysQuery
