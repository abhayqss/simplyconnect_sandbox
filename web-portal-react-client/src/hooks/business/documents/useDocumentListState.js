import { useListState } from 'hooks/common'

import Entity from 'entities/DocumentCombinedFilter'

export default function useDocumentListState() {
    return useListState({ filterEntity: Entity })
}