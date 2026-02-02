import { useMemo } from 'react'

import useDetails from 'hooks/common/useDetails'

import service from 'services/ClientService'

const options = {
    doLoad: ({ clientId }) => service.findById(clientId),
}

//@deprecated
function useClientDetails(clientId) {
    const params = useMemo(() => ({ clientId }), [clientId])

    return useDetails('CLIENT', params, options)
}

export default useClientDetails
