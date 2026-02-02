import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findAppointmentServiceCategories(params)

export default function useAppointmentServiceCategoriesQuery(params, options) {
    return useQuery(['Directory.AppointmentServiceCategories', params], () => fetch(params), options)
}
