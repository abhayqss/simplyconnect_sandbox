import { useQuery } from 'hooks/common'

import service from 'services/DirectoryService'

const fetch = params => service.findClientProgramNoteTypes(params)

function useClientProgramNoteTypesQuery(params, options) {
    return useQuery('ClientProgramNoteType', params, {
        fetch,
        ...options,
    })
}

export default useClientProgramNoteTypesQuery
