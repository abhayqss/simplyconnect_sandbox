import { useSharedCache } from 'hooks/common'

const NAME = 'DOCUMENT_FILTER_DEFAULT_DATA'

export default function useDocumentFilterDefaultDataCache(params) {
    const cache = useSharedCache([NAME, params])

    function get(o) {
        return cache.get([NAME, o ?? params])
    }

    return { ...cache, get }
}