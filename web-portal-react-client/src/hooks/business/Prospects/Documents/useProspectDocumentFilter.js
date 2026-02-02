import Entity from 'entities/DocumentFilter'
import { useCustomFilter } from 'hooks/common/filter'

function useProspectDocumentFilter(options) {
    return useCustomFilter('PROSPECT_DOCUMENT_FILTER', Entity, options)
}

export default useProspectDocumentFilter