import { useQuery } from '@tanstack/react-query'

import service from 'services/ClientExpenseService'

function fetch(params) {
	return service.count(params)
}

function useClientExpenseCountQuery(params, options) {
	return useQuery(['Client.ExpenseCount', params], () => fetch(params), options)
}

export default useClientExpenseCountQuery
