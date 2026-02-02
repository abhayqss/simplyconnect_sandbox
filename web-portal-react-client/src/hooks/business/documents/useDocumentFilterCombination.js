import { without } from 'underscore'

import { DocumentFilterValidator as Validator } from 'validators'

import { useMutationWatch } from 'hooks/common'
import { useFilterCombination } from 'hooks/common/filter'

import DocumentFilter from 'entities/DocumentFilter'

import { useDocumentFilterDefaultDataCache } from './'

let organizationId = null
let communityId = null

function purifyData(data) {
    return {
        ...data,
        categoryIds: without(
            data.categoryIds, 'NO'
        )
    }
}

export default function useDocumentFilterCombination(primary, custom) {
    const cache = useDocumentFilterDefaultDataCache()

    return useFilterCombination(
        {
            name: 'DOCUMENT_PRIMARY_FILTER',
            isCommunityMultiSelection: false,
            ...primary,
            onChange: data => {
                primary.onChange(data)
                organizationId = data.organizationId
                communityId = data.communityId
            }
        },
        {
            name: 'DOCUMENT_CUSTOM_FILTER',
            entity: DocumentFilter,
            Validator,
            canReApply: true,
            getDefaultData: () => cache.get({
                organizationId, communityId
            }),
            ...custom,
            onChange: data => {
                custom.onChange(purifyData(data))
            }
        }
    )
}