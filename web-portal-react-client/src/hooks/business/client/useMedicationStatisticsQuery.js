import { useQuery } from 'hooks/common'

import service from 'services/ClientMedicationService'

const fetch = params => service.statistics(params)

export default function useMedicationStatisticsQuery(params, options) {
    return useQuery('MedicationStatistics', params, { ...options, fetch })
}