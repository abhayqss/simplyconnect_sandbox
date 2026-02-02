import { useSharedCache } from 'hooks/common'

export default function useProspectDocumentFilterDefaultDataCache(params) {
    return useSharedCache(['PROSPECT_DOCUMENT_FILTER_DEFAULT_DATA', params])
}