import { useListState } from 'hooks/common'

import Entity from 'entities/ProspectEventFilter'

export default function useProspectEventListState() {
    return useListState({ filterEntity: Entity })
}