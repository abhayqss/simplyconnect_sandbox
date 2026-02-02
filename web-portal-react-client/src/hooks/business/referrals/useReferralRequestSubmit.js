import { useMutation } from '@tanstack/react-query'

import service from 'services/ReferralService'

function submit(data) {
    return service.save(data)
}

function useReferralRequestSubmit(options) {
    return useMutation(submit, options)
}

export default useReferralRequestSubmit
