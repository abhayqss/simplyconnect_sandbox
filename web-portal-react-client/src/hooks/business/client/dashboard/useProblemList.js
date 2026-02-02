import { useMemo } from 'react'

import useList from 'hooks/common/useList'

import service from 'services/ClientProblemService'

const options = {
    isMinimal: true,
    doLoad: ({ clientId, ...params }) => service.find(clientId, params)
}

function useProblemList({ clientId, includeOther, includeActive, includeResolved }) {
    const params = useMemo(() => ({
        clientId, includeOther, includeActive, includeResolved
    }), [clientId, includeOther, includeActive, includeResolved])
    
    return useList('CLIENT_PROBLEM', params, options)
}

export default useProblemList
