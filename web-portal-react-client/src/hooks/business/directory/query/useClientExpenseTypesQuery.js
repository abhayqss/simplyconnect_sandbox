import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findClientExpenseTypes(params)

function useClientExpenseTypesQuery(params, options) {
	return useQuery(['Directory.ClientExpenseTypes', params], () => fetch(params), options)
}

export default useClientExpenseTypesQuery
