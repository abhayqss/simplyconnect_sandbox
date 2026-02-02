import { useQuery } from '@tanstack/react-query'

import service from 'services/ClientExpenseService'

function fetch(params) {
	return service.canAdd(params)
}

function useCanAddClientExpenseQuery(params, options) {
	return useQuery(['Client.CanAddExpense', params], () => fetch(params), options)
}

export default useCanAddClientExpenseQuery
