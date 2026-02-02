import { useListState } from 'hooks/common'

import Entity from 'entities/ProviderFilter'

export default function useProviderListState() {
    return useListState({ filterEntity: Entity })
}