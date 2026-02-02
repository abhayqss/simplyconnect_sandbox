import {
    isEqual
} from 'underscore'

import {
    useMemoEffect
} from 'hooks/common'

import {
    useFilterCombination
} from 'hooks/common/filter'

import ContactFilter from 'entities/ContactFilter'

import { useContactFilterDefaultDataCache } from './'

let organizationId

export default function useContactFilterCombination(primary, custom, data) {
    const cache = useContactFilterDefaultDataCache()

    const combination = useFilterCombination(
        {
            name: 'CONTACT_PRIMARY_FILTER',
            customFields: { includeWithoutCommunity: null },
            ...primary,
            onChange: data => {
                primary.onChange(data)
                organizationId = data.organizationId
            }
        },
        {
            name: 'CONTACT_CUSTOM_FILTER',
            entity: ContactFilter,
            getDefaultData: () => cache.get({ organizationId }),
            canReApply: true,
            ...custom
        }
    )

    const { apply } = combination.primary

    useMemoEffect(memo => {
        const prev = memo()

        if (prev
            && isEqual(data.communityIds, prev.communityIds)
            && data.includeWithoutCommunity !== prev.includeWithoutCommunity) {
            apply()
        }

        memo(data)
    }, [data, apply])

    return combination
}