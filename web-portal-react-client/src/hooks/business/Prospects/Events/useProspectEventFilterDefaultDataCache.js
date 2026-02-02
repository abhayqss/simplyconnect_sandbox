import { useSharedCache } from 'hooks/common'

const NAME = 'PROSPECT_EVENT_FILTER_DEFAULT_DATA'

export default function useProspectEventFilterDefaultDataCache(params) {
    const cache = useSharedCache([NAME, params])

    function get(o) {
        return cache.get([NAME, o ?? params])
    }

    return { ...cache, get }
}