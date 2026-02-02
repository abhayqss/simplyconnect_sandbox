import { useMemo } from 'react'

import useList from 'hooks/common/useList'

import service from 'services/DirectoryService'

const options = {
    doLoad: ({ organizationId }) => service.findCommunityNames({ organizationId }),
    isMinimal: true,
}

function useCommunityNameList({ organizationId }) {
    const params = useMemo(() => ({ organizationId }), [organizationId])
    const { state, fetch } = useList('COMMUNITY_NAME', params, options)

    return { state, fetch }
}

export default useCommunityNameList
