import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findOrganizationCanhaveHousingVouchers(
    params, { response: { extractDataOnly: true } }
)

export default function useCanHaveHousingVouchersQuery(params, options) {
    return useQuery(['OrganizationsCanHaveHousingVouchers', params], () => fetch(params), options)
}