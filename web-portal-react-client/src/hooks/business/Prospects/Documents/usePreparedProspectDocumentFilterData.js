import { useMemo } from 'react'

import { isNull } from 'underscore'
import { useQueryClient } from '@tanstack/react-query'

import { isEqLn, isNotEmpty } from 'lib/utils/Utils'

export default function usePreparedProspectDocumentFilterData(data) {
    const {
        prospectId,
        categoryIds = [],
        signatureStatusNames,
        includeNotCategorized,
        includeWithoutSignature
    } = data

    const client = useQueryClient()

    const prospect = client.getQueryData(
        ['Prospect', { prospectId }]
    )

    const { organizationId } = prospect ?? {}

    const categories = client.getQueryData(
        ['Directory.DocumentCategories', { organizationId }]
    )

    const areAllCategories = isEqLn(categories, categoryIds)

    const signatureStatuses = client.getQueryData(
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