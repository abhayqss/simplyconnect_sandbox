import { useMutation } from 'hooks/common'

import service from 'services/ProspectService'

const fetch = params => service.activate(params)

function useProspectActivation(params, options) {
    return useMutation(params, fetch, options)
}

export default useProspectActivation
