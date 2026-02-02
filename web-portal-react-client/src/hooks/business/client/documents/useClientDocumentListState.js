import { useListState } from 'hooks/common'

import Entity from 'entities/DocumentFilter'

export default function useClientDocumentListState() {
    return useListState({ filterEntity: Entity })
}