import { useFilterCombination } from 'hooks/common/filter'

import CommunityFilter from 'entities/CommunityFilter'

import { useCommunityFilterDefaultDataCache } from '.'

let organizationId

export default function useCommunityFilterCombination(primary, custom) {
    const cache = useCommunityFilterDefaultDataCache()

    return useFilterCombination(
        {
            name: 'COMMUNITY_PRIMARY_FILTER',
            ...primary,
            onChange: data => {
                primary.onChange(data)
                organizationId = data.organizationId
            }
        },
        {
            name: 'COMMUNITY_CUSTOM_FILTER',
            entity: CommunityFilter,
            getDefaultData: () => cache.get({ organizationId }),
            canReApply: true,
            ...custom
        }
    )
}