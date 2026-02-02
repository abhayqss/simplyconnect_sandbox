import { useMutation } from 'hooks/common'

import service from 'services/ProspectService'

function useProspectSubmit(params, options) {
	return useMutation(params, data => service.save(data), options)
}

export default useProspectSubmit
