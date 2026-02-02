import { useQuery } from '@tanstack/react-query'

import service from 'services/OrganizationService'

const fetch = ({ organizationId, isMarketplaceDataIncluded }) => (
    service.findById(organizationId, { isMarketplaceDataIncluded }, {
        response: { extractDataOnly: true }
    })
)

function useOrganizationQuery(params, options) {
    return useQuery(['Organization', params], () => fetch(params), options)
}

export default useOrganizationQuery
