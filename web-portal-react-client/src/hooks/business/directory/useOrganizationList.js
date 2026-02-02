import useList from 'hooks/common/useList'

import service from 'services/DirectoryService'

const params = {}

const options = {
    doLoad: () => service.findOrganizations(),
    isMinimal: true,
}

function useOrganizationList() {
    const { state, fetch } = useList('ORGANIZATION', params, options)

    return { state, fetch }
}

export default useOrganizationList
