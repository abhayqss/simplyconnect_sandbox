import { useQuery } from '@tanstack/react-query'

import service from 'services/ClientExpenseService'

function fetch(params) {
	return service.total(params)
}

function useClientExpenseTotalQuery(params, options) {
	return useQuery(['Client.ExpenseTotal', params], () => fetch(params), options)
}

export default useClientExpenseTotalQuery
