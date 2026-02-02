import { useMutation } from 'hooks/common'

import service from 'services/ClientAccessRequestService'

function submit(params) {
	return service.save(params)
}

function useClientAccessRequestSubmit(params, options) {
	return useMutation(params, submit, options)
}

export default useClientAccessRequestSubmit