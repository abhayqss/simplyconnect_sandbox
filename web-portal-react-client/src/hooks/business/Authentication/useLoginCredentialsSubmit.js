import { useMutation } from 'hooks/common'

import service from 'services/AuthService'

function submit(data) {
	return service.login(data)
}

function useLoginCredentialsSubmit(params, options) {
	return useMutation(params, submit, options)
}

export default useLoginCredentialsSubmit
