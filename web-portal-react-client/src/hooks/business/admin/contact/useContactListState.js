import { useListState } from 'hooks/common'

import Entity from 'entities/ContactCombinedFilter'

export default function useContactListState() {
    return useListState({ filterEntity: Entity })
}