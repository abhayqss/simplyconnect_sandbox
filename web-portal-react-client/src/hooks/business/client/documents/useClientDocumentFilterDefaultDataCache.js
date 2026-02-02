import { useSharedCache } from 'hooks/common'

export default function useClientDocumentFilterDefaultDataCache(params) {
    return useSharedCache(['CLIENT_DOCUMENT_FILTER_DEFAULT_DATA', params])
}