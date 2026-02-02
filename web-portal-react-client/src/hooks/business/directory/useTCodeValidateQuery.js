import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.validateHousingVouchersTcode(
    params, { response: { extractDataOnly: true } }
)

export default function useTCodeValidateQuery(params, options) {
    return useQuery(['OrganizationsValidateHousingVouchersTcode', params], () => fetch(params), options)
}