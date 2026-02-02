import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

const fetch = params => service.findESignRequestedDocuments(params)

function useESignRequestedDocumentsQuery(params, options) {
	return useQuery({
		queryKey: ['ESignRequestedDocuments', params],
		queryFn: () => fetch(params),
		...options
	})
}

export default useESignRequestedDocumentsQuery