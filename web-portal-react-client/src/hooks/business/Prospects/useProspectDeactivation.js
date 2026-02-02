import { useMutation } from 'hooks/common'

import service from 'services/ProspectService'

const fetch = params => service.deactivate(params)

function useProspectDeactivation(params, options) {
    return useMutation(params, fetch, options)
}

export default useProspectDeactivation
