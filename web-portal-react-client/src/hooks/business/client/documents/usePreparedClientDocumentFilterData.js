import { useMemo } from 'react'

import { isNull } from 'underscore'
import { useQueryClient } from '@tanstack/react-query'

import { isEqLn, isNotEmpty } from 'lib/utils/Utils'

export default function usePreparedClientDocumentFilterData(data) {
    const {
        clientId,
        categoryIds = [],
        signatureStatusNames,
        includeNotCategorized,
        includeWithoutSignature
    } = data

    const queryClient = useQueryClient()

    const client = queryClient.getQueryData(
        ['Client', { clientId }]
    )

    const { organizationId } = queryClient ?? {}

    const categories = queryClient.getQueryData(
        ['Directory.DocumentCategories', { organizationId }]
    )

    const areAllCategories = isEqLn(categories, categoryIds)

    const signatureStatuses = queryClient.getQueryData(
        ['DocumentSignatureStatuses', { organizationId }]
    )

    const areAllSignatureStatuses = isEqLn(signatureStatuses, signatureStatusNames)

    return useMemo(() => ({
        ...data,
        ...areAllCategories && {
            ...isNotEmpty(categoryIds) && {
                includeNotCategorized: (
                    isNull(includeNotCategorized)
                    || includeNotCategorized
                )
            }
        },
        ...areAllSignatureStatuses && {
            ...isNotEmpty(signatureStatusNames) && {
                includeWithoutSignature: (
                    isNull(includeWithoutSignature)
                    || includeWithoutSignature
                )
            }
        }
    }), [
        data,
        categoryIds,
        areAllCategories,
        includeNotCategorized,
        signatureStatusNames,
        areAllSignatureStatuses,
        includeWithoutSignature
    ])
}