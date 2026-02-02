import { useManualPaginatedQuery } from 'hooks/common'

import service from 'services/ClientExpenseService'

const fetch = params => service.find(params)

export default function useClientExpensesQuery(params, options) {
	return useManualPaginatedQuery({ size: 15, ...params }, fetch, options)
}