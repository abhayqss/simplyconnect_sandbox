import { useMemo } from 'react'

import useList from 'hooks/common/useList'

import service from 'services/DirectoryService'

const options = {
    doLoad: ({ organizationId }) => service.findCommunities({ organizationId }),
    isMinimal: true,
}

function useCommunityList({ organizationId }) {
    const params = useMemo(() => ({ organizationId }), [organizationId])
    const { state, fetch } = useList('COMMUNITY', params, options)

    return { state, fetch }
}

export default useCommunityList
