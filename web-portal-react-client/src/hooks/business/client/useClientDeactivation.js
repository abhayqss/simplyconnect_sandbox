import { useMutation } from 'hooks/common'

import service from 'services/ClientService'

const fetch = params => service.deactivate(params)

function useClientDeactivation(params, options) {
    return useMutation(params, fetch, options)
}

export default useClientDeactivation
