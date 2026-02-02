import { useFilterCombination } from 'hooks/common/filter'

import ProspectFilter from 'entities/ProspectFilter'

import { useProspectFilterDefaultDataCache } from '.'

import {
    PROSPECT_STATUSES
} from 'lib/Constants'

const {
    ACTIVE
} = PROSPECT_STATUSES

let organizationId

export default function useProspectFilterCombination(primary, custom) {
    const cache = useProspectFilterDefaultDataCache()

    return useFilterCombination(
        {
            name: 'PROSPECT_PRIMARY_FILTER',
            ...primary,
            onChange: data => {
                primary.onChange(data)
                organizationId = data.organizationId
            }
        },
        {
            name: 'PROSPECT_CUSTOM_FILTER',
            entity: ProspectFilter,
            getDefaultData: () => ({
                statuses: [ACTIVE],
                ...cache.get()
            }),
            canReApply: true,
            ...custom
        }
    )
}