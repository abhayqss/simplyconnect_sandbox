import { useQuery } from '@tanstack/react-query'

import service from 'services/PaperlessHealthcareService'

const fetch = () => service.canView()

export default function useCanViewPaperlessHealthcareQuery(params, options) {
    return useQuery(['PaperlessHealthcare.CanView', params], () => fetch(params), options)
}