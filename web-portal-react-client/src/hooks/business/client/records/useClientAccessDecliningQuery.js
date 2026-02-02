import { useMutation } from 'hooks/common'

import service from 'services/ClientAccessRequestService'

const fetch = params => service.decline(params)

function useClientAccessDecliningQuery(params, options) {
	return useMutation(params, fetch, options)
}

export default useClientAccessDecliningQuery