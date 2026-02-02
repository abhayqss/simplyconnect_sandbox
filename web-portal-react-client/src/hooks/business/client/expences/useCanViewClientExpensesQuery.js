import { useQuery } from '@tanstack/react-query'

import service from 'services/ClientExpenseService'

function fetch(params) {
	return service.canView(params)
}

function useCanViewClientExpensesQuery(params, options) {
	return useQuery(['Client.CanViewExpenses', params], () => fetch(params), options)
}

export default useCanViewClientExpensesQuery
