import { useMutation } from '@tanstack/react-query'

import service from 'services/PaperlessHealthcareService'

export default function usePaperlessHealthcareDemoRequestSubmit(options) {
    return useMutation((tileName) => service.requestDemo(tileName), options)
}
