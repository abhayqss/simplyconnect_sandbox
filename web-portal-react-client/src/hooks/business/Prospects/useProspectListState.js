import { useListState } from 'hooks/common'

import Entity from 'entities/ProspectCombinedFilter'

export default function useProspectListState() {
    return useListState({ filterEntity: Entity })
}