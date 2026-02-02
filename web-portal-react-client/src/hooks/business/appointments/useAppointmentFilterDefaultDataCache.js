import { useSharedCache } from 'hooks/common'

const NAME = 'APPOINTMENT_FILTER_DEFAULT_DATA'

export default function useAppointmentFilterDefaultDataCache(params) {
    const cache = useSharedCache([NAME, params])

    function get(o) {
        return cache.get([NAME, o ?? params])
    }

    return { ...cache, get }
}