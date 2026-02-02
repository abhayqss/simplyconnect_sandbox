import { useListState } from 'hooks/common'

import Entity from 'entities/DocumentFilter'

export default function useProspectDocumentListState() {
    return useListState({ filterEntity: Entity })
}