import useList from 'hooks/common/useList'

import service from 'services/DirectoryService'

const options = {
    doLoad: (params) => {
        return service.findClients({ ...params, recordStatuses: ['ACTIVE'] })
    },
    isMinimal: true,
}

function useActiveClientList(params) {
    const { state, fetch } = useList('ACTIVE_CLIENTS', params, options)

    return { state, fetch }
}

export default useActiveClientList
