import { useFilterCombination } from 'hooks/common/filter'

import AppointmentFilter from 'entities/AppointmentFilter'

import { useAppointmentFilterDefaultDataCache } from '.'

let organizationId

export default function useAppointmentFilterCombination(primary, custom) {
    const cache = useAppointmentFilterDefaultDataCache()

    return useFilterCombination(
        {
            name: 'APPOINTMENT_PRIMARY_FILTER',
            ...primary,
            onChange: data => {
                primary.onChange(data)
                organizationId = data.organizationId
            }
        },
        {
            name: 'APPOINTMENT_CUSTOM_FILTER',
            entity: AppointmentFilter,
            getDefaultData: () => cache.get({ organizationId }),
            canReApply: true,
            ...custom
        }
    )
}