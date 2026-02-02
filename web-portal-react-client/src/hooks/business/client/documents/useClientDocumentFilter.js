import Entity from 'entities/DocumentFilter'
import { useCustomFilter } from 'hooks/common/filter'

function useClientDocumentFilter(options) {
    return useCustomFilter('CLIENT_DOCUMENT_FILTER', Entity, options)
}

export default useClientDocumentFilter