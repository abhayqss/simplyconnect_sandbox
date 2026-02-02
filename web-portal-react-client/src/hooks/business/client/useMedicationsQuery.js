import { useQuery } from 'hooks/common'

import service from 'services/ClientMedicationService'

const fetch = params => service.find(params)

export default function useMedicationsQuery(params, options) {
    return useQuery('Medications', params, { ...options, fetch })
}