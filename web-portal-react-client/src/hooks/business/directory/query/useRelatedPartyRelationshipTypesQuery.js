import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = (params) => service.findRelatedPartyRelationshipTypes(params)

function useRelatedPartyRelationshipTypesQuery(params, options) {
    return useQuery(['Directory.RelationshipTypes', params], () => fetch(params), options)
}

export default useRelatedPartyRelationshipTypesQuery
