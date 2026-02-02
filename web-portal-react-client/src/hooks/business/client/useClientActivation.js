import { useMutation } from 'hooks/common'

import service from 'services/ClientService'

const fetch = params => service.activate(params)

function useClientActivation(params, options) {
    return useMutation(params, fetch, options)
}

export default useClientActivation
