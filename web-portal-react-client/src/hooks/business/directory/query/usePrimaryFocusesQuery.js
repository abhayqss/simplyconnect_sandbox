import { useQuery } from 'hooks/common'

import service from 'services/DirectoryService'

const fetch = params => service.findPrimaryFocuses(params)

function usePrimaryFocusesQuery(params, options) {
    return useQuery('PrimaryFocuses', params, {
        fetch,
        ...options,
    })
}

export default usePrimaryFocusesQuery
