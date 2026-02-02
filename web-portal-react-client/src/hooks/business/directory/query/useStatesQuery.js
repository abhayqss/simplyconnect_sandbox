import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findStates(params, {
    response: { extractDataOnly: true }
})

function useStatesQuery(params, options) {
    return useQuery(['States', params], () => fetch(params), options)
}

export default useStatesQuery
