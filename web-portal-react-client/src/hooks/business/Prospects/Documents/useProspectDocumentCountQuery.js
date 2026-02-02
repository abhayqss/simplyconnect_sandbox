import { useQuery } from '@tanstack/react-query'

import service from 'services/ProspectDocumentService'

const fetch = (params) => service.count(params)

export default function useProspectDocumentCountQuery(params, options) {
	return useQuery(['ProspectDocumentCount', params], () => fetch(params), options)
}