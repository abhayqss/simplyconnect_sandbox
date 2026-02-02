import { useMemo } from 'react'

import { isNull, omit } from 'underscore'
import { useQueryClient } from '@tanstack/react-query'

import { isEqLn, isNotEmpty } from 'lib/utils/Utils'

export default function usePreparedDocumentFilterData(data) {
    const {
        organizationId,
        categoryIds = [],
        includeNotCategorized
    } = data

    const queryClient = useQueryClient()

    const categories = queryClient.getQueryData(
        ['Directory.DocumentCategories', { organizationId }]
    )

    const areAllCategories = isEqLn(categories, categoryIds)

    return useMemo(() => ({
        ...omit(data, 'isSecurityEnabled'),
        ...areAllCategories && {
            ...isNotEmpty(categoryIds) && {
                includeNotCategorized: (
                    isNull(includeNotCategorized)
                    || includeNotCategorized
                )
            }
        }
    }), [
        data,
        categoryIds,
        areAllCategories,
        includeNotCategorized
    ])
}