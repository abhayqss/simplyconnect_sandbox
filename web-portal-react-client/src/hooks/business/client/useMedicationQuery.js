import { useQuery } from 'hooks/common'

import service from 'services/ClientMedicationService'

const fetch = ({ medicationId, ...params }) => (
    service.findById(medicationId, params)
)

export default function useMedicationQuery(params, options) {
    return useQuery('Medication', params, { ...options, fetch })
}