import { useQuery } from 'hooks/common'

import service from 'services/DirectoryService'

const fetch = params => service.findClientPharmacyNames(params)

function useClientPharmacyNamesQuery(params, options) {
    return useQuery('ClientPharmacyNames', params, {
        fetch,
        ...options,
    })
}

export default useClientPharmacyNamesQuery
