import Entity from 'entities/ProspectEventFilter'
import { useCustomFilter } from 'hooks/common/filter'

function useProspectEventFilter(options) {
    return useCustomFilter('PROSPECT_EVENT_FILTER', Entity, options)
}

export default useProspectEventFilter