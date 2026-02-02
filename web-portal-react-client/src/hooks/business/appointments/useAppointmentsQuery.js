import { useManualPaginatedQuery } from 'hooks/common'

import { PAGINATION } from 'lib/Constants'
import service from 'services/AppointmentService'

const fetch = params => service.find(params)

const { MAX_SIZE } = PAGINATION

export default function useAppointmentsQuery(params, options) {
    return useManualPaginatedQuery({ size: MAX_SIZE, ...params }, fetch, options)
}