import { useMutation } from 'hooks/common'

import service from 'services/ClientExpenseService'

function submit({ clientId, ...data } = {}) {
	return service.save(data, { clientId })
}

function useClientExpenseSubmit(params, options) {
	return useMutation(params, submit, options)
}

export default useClientExpenseSubmit
