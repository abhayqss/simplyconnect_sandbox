import { useMutation } from 'hooks/common'

import service from 'services/ClientAccessRequestService'

const submit = params => service.approve(params)

function useClientAccessApprovingQuery(params, options) {
	return useMutation(params, submit, options)
}

export default useClientAccessApprovingQuery