import { useQuery } from '@tanstack/react-query'

import service from 'services/ClientExpenseService'

function fetch({ expenseId, ...params }) {
	return service.findById(expenseId, params)
}

function useClientExpenseQuery(params, options) {
	return useQuery(['Client.Expense', params], () => fetch(params), options)
}

export default useClientExpenseQuery
